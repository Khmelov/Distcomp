using Microsoft.EntityFrameworkCore;
using rest1.application.exceptions.db;
using rest1.application.interfaces;
using rest1.core.entities;

namespace rest1.persistence.db.repositories;

public class DbCreatorRepository : DbRepository<Creator>, ICreatorRepository
{
    public DbCreatorRepository(RestServiceDbContext dbContext) : base(dbContext)
    {
    }

    public override async Task<Creator> AddAsync(Creator entity, CancellationToken cancellationToken = default)
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
