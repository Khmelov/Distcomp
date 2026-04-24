using Application.Exceptions.Persistance;
using Application.Interfaces;
using Core.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Persistence.EFCore
{
    public class EditorEfRepository : EfRepository<Editor>, IEditorRepository
    {
        public EditorEfRepository(AppDbContext dbContext) : base(dbContext)
        {
        }

        public override async Task<Editor> AddAsync(Editor entity, CancellationToken cancellationToken = default)
        {

            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            var existingEntity = await _dbSet.Where(e => e.Id == entity.Id || e.Login == entity.Login).FirstOrDefaultAsync(cancellationToken);

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
    }
}