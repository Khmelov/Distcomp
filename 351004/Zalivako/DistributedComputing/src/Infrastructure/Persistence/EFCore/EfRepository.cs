using Application.Interfaces;
using Core.Entities;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using Application.Exceptions;
using Application.Exceptions.Persistance;

namespace Infrastructure.Persistence.EFCore
{
    public class EfRepository<TEntity> : IRepository<TEntity>
        where TEntity : Entity
    {
        protected readonly DbSet<TEntity> _dbSet;
        protected readonly DbContext _dbContext;

        public EfRepository(AppDbContext dbContext)
        {
            _dbContext = dbContext ?? throw new ArgumentNullException(nameof(dbContext));
            _dbSet = dbContext.Set<TEntity>();
        }

        public virtual async Task<TEntity?> FindAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            IQueryable<TEntity> query = ApplySpecification(specification);
            return await query.FirstOrDefaultAsync(cancellationToken);
        }

        public virtual async Task<IEnumerable<TEntity>> FindAllAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            IQueryable<TEntity> query = ApplySpecification(specification);
            return await query.ToListAsync(cancellationToken);
        }

        public virtual async Task<int> CountAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            IQueryable<TEntity> query = ApplySpecification(specification);
            return await query.CountAsync(cancellationToken);
        }

        public virtual async Task<bool> AnyAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            IQueryable<TEntity> query = ApplySpecification(specification);
            return await query.AnyAsync(cancellationToken);
        }

        // CRUD 
        public virtual async Task<TEntity> AddAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            var existingEntity = await _dbSet.FindAsync([entity.Id], cancellationToken: cancellationToken);

            if (existingEntity != null)
            {
                throw new InvalidOperationException($"Entity with id {entity.Id} already exists");
            }

            var entry = await _dbSet.AddAsync(entity, cancellationToken);
            try
            {
                await _dbContext.SaveChangesAsync(cancellationToken);
            }
            catch (DbUpdateException ex) when (IsForeignKeyViolation(ex))
            {
                throw new ForeignKeyViolationException("Foreign key doesn't exist");
            }

            return entry.Entity;
        }

        protected static bool IsForeignKeyViolation(DbUpdateException ex)
        {
            return ex.InnerException switch
            {
                PostgresException pgEx => pgEx.SqlState == "23503",
                _ => false
            };
        }

        public virtual async Task<TEntity?> UpdateAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            var existingEntity = await _dbSet.FindAsync([entity.Id, cancellationToken], cancellationToken: cancellationToken);

            if (existingEntity == null)
                return null;

            _dbContext.Entry(existingEntity).CurrentValues.SetValues(entity);
            await _dbContext.SaveChangesAsync(cancellationToken);

            return existingEntity;
        }

        public virtual async Task<TEntity?> DeleteAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            var existingEntity = await _dbSet.FindAsync(entity.Id, cancellationToken);

            if (existingEntity == null)
                return null;

            _dbSet.Remove(existingEntity);
            await _dbContext.SaveChangesAsync(cancellationToken);

            return existingEntity;
        }

        public virtual async Task<bool> DeleteByIdAsync(long id, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            var entity = await _dbSet.FindAsync(id , cancellationToken);

            if (entity == null)
                return false;

            _dbSet.Remove(entity);
            await _dbContext.SaveChangesAsync(cancellationToken);

            return true;
        }

        public virtual async Task<TEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            return await _dbSet.FindAsync(id, cancellationToken);
        }

        public virtual async Task<IEnumerable<TEntity>> GetAllAsync(CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            return await _dbSet.ToListAsync(cancellationToken);
        }

        protected virtual IQueryable<TEntity> ApplySpecification(ISpecification<TEntity> spec)
        {
            IQueryable<TEntity> query = _dbSet.AsQueryable();

            if (spec.Criteria != null)
            {
                query = query.Where(spec.Criteria);
            }

            // Применяем инклюды (включаемые навигационные свойства)
            query = spec.Includes
                .Aggregate(query, (current, include) => current.Include(include));

            if (spec.OrderBy != null)
            {
                query = query.OrderBy(spec.OrderBy);
            }
            else if (spec.OrderByDescending != null)
            {
                query = query.OrderByDescending(spec.OrderByDescending);
            }

            if (spec.IsPagingEnabled)
            {
                query = query.Skip(spec.Skip ?? 0)
                            .Take(spec.Take ?? 10);
            }

            return query;
        }
    }
}