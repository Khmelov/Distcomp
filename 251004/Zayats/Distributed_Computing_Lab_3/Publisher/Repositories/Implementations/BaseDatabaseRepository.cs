using Microsoft.EntityFrameworkCore;
using Publisher.Data;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public abstract class BaseDatabaseRepository<TEntity> : IRepository<TEntity>
    where TEntity : BaseModel
{
    protected readonly AppDbContext _context;
    protected readonly DbSet<TEntity> _dbSet;

    public BaseDatabaseRepository(AppDbContext context)
    {
        _context = context;
        _dbSet = context.Set<TEntity>();
    }
    
    public virtual async Task<IEnumerable<TEntity>> GetAllAsync()
        => await _dbSet.ToListAsync();

    public virtual async Task<TEntity?> GetByIdAsync(long id)
        => await _dbSet.FirstOrDefaultAsync(x => x.Id == id);

    public virtual async Task<TEntity> CreateAsync(TEntity entity)
    {
        var newEntity = await _dbSet.AddAsync(entity);
        await _context.SaveChangesAsync();
        return newEntity.Entity;
    }

    public virtual async Task<TEntity?> UpdateAsync(TEntity entity)
    {
        var newEntity = _dbSet.Update(entity);
        await _context.SaveChangesAsync();
        return newEntity.Entity;
    }

    public virtual async Task<bool> DeleteAsync(long id)
    {
        var entity = await GetByIdAsync(id);
        if (entity is null)
        {
            return false;
        }

        _dbSet.Remove(entity);
        await _context.SaveChangesAsync();
        return true;
    }
}