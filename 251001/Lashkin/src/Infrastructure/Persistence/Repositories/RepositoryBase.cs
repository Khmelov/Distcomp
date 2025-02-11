using System.Linq.Expressions;
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

    public IQueryable<T> FindAll(bool trackChanges)
    {
        if (trackChanges)
        {
            return _context.Set<T>();
        }
        
        return _context.Set<T>().AsNoTracking();
    }

    public IQueryable<T> FindByCondition(Expression<Func<T, bool>> expression, bool trackChanges)
    {
        if (trackChanges)
        {
            var entity = _context.Set<T>().Where(expression).AsNoTracking();
        }

        return _context.Set<T>().Where(expression);
    }

    public void Create(T entity)
    {
        _context.Set<T>().Add(entity);
    }

    public void Update(T entity)
    {
        _context.Set<T>().Update(entity);
    }

    public void Delete(T entity)
    {
        _context.Set<T>().Remove(entity);
    }
}