using Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public abstract class RepositoryBase<T> : IRepositoryBase<T> where T : class
{
    private readonly RepositoryContext _context;

    public RepositoryBase(RepositoryContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<T>> GetAll(bool trackChanges, CancellationToken cancellationToken = default)
    {
        if (trackChanges)
        {
            return await _context.Set<T>().ToListAsync(cancellationToken);
        }
        
        return await _context.Set<T>().AsNoTracking().ToListAsync(cancellationToken);
    }

    public async Task<T?> FindByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Set<T>().FindAsync(new object[] { id }, cancellationToken);
        
        return entity;
    }

    public async Task CreateAsync(T entity, CancellationToken cancellationToken = default)
    {
        await _context.Set<T>().AddAsync(entity, cancellationToken);
    }

    public void UpdateAsync(T entity)
    {
        _context.Set<T>().Update(entity);
    }

    public void DeleteAsync(T entity)
    {
        _context.Set<T>().Remove(entity);
    }
}