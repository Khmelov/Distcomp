using Application.Exceptions.Persistance;
using Application.Interfaces;
using Core.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Persistence.EFCore
{
    public class NewsEfRepository : EfRepository<News>, INewsRepository
    {
        public NewsEfRepository(AppDbContext dbContext) : base(dbContext)
        {

        }

        public override async Task<News> AddAsync(News entity, CancellationToken cancellationToken = default)
        {

            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();
            
            var existingEntity = await _dbSet.Where(n => n.Id == entity.Id || n.Title == entity.Title).FirstOrDefaultAsync(cancellationToken);

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

        public async override Task<News?> DeleteAsync(News entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            var existingEntity = await _dbSet.Where(n => n.Id == entity.Id)
                .Include(n => n.Markers)
                .FirstOrDefaultAsync();

            if (existingEntity == null)
                return null;

            _dbSet.Remove(existingEntity);

            await _dbContext.SaveChangesAsync(cancellationToken);

            return existingEntity;
        }

        public async Task<News> GetByIdWithMarkersAsync(int id)
        {
            return await _dbSet
                .Include(n => n.Markers)
                .FirstOrDefaultAsync(n => n.Id == id);
        }

        public async Task RemoveAsync(News news)
        {
            _dbSet.Remove(news);
            await _dbContext.SaveChangesAsync();
        }
    }
}