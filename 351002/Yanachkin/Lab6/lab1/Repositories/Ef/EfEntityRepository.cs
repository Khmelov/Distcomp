using System.Linq.Expressions;
using lab1.Common.Paging;
using lab1.Data;
using lab1.Models.Entities;
using lab1.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace lab1.Repositories.Ef;

public class EfEntityRepository<T> : IEntityRepository<T>
    where T : BaseEntity
{
    protected readonly AppDbContext Db;
    protected readonly DbSet<T> Set;

    public EfEntityRepository(AppDbContext db)
    {
        Db = db;
        Set = db.Set<T>();
    }

    public virtual Task<T?> GetByIdAsync(long id, CancellationToken cancellationToken = default)
        => Set.FirstOrDefaultAsync(e => e.Id == id, cancellationToken);

    public async Task<T> AddAsync(T entity, CancellationToken cancellationToken = default)
    {
        await Set.AddAsync(entity, cancellationToken);
        await Db.SaveChangesAsync(cancellationToken);
        return entity;
    }

    public async Task<T> UpdateAsync(T entity, CancellationToken cancellationToken = default)
    {
        if (Db.Entry(entity).State == EntityState.Detached)
            Set.Update(entity);

        await Db.SaveChangesAsync(cancellationToken);
        return entity;
    }

    public virtual async Task DeleteByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var deleted = await Set.Where(e => e.Id == id).ExecuteDeleteAsync(cancellationToken);
        if (deleted == 0)
            throw new KeyNotFoundException($"{typeof(T).Name} not found");
    }

    public virtual async Task<PagedResult<T>> GetPagedAsync(
        Expression<Func<T, bool>>? filter,
        Func<IQueryable<T>, IOrderedQueryable<T>>? orderBy,
        int page,
        int size,
        CancellationToken cancellationToken = default)
    {
        var safeSize = size <= 0 ? 20 : Math.Min(size, 200);
        var safePage = page < 0 ? 0 : page;

        IQueryable<T> query = Set.AsNoTracking();
        if (filter != null)
            query = query.Where(filter);

        var total = await query.CountAsync(cancellationToken);

        query = orderBy != null ? orderBy(query) : query.OrderBy(e => e.Id);

        var items = await query
            .Skip(safePage * safeSize)
            .Take(safeSize)
            .ToListAsync(cancellationToken);

        return PagedResult<T>.Create(items, total, safePage, safeSize);
    }

    public virtual async Task<IReadOnlyList<T>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return await Set.AsNoTracking()
            .OrderBy(e => e.Id)
            .ToListAsync(cancellationToken);
    }
}
