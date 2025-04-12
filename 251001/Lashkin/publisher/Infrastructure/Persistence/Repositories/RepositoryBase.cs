using System.Linq.Expressions;
using Domain.Entities;
using Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public class RepositoryBase<T> : IRepositoryBase<T> where T : BaseEntity
{
    private readonly RepositoryContext _context;

    public RepositoryBase(RepositoryContext context)
    {
        _context = context;
    }

    public IQueryable<T> FindAll(bool trackChanges)
    {
        return trackChanges ? _context.Set<T>() : _context.Set<T>().AsNoTracking();
    }

    public IQueryable<T> FindByCondition(Expression<Func<T, bool>> expression, bool trackChanges)
    {
        return trackChanges ? _context.Set<T>().Where(expression) : _context.Set<T>().Where(expression).AsNoTracking();
    }

    public async Task<T?> FindByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        return await _context.FindAsync<T>([id], cancellationToken);
    }

    public void Create(T entity)
    {
        _context.Add(entity);
    }

    public void Update(T entity)
    {
        _context.Update(entity);
    }

    public void Delete(T entity)
    {
        _context.Remove(entity);
    }
}