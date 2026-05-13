using System.Linq.Expressions;
using lab1.Common.Paging;
using lab1.Models.Entities;

namespace lab1.Repositories.Interfaces;

public interface IEntityRepository<T>
    where T : BaseEntity
{
    Task<T?> GetByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<T> AddAsync(T entity, CancellationToken cancellationToken = default);

    Task<T> UpdateAsync(T entity, CancellationToken cancellationToken = default);

    Task DeleteByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<PagedResult<T>> GetPagedAsync(
        Expression<Func<T, bool>>? filter,
        Func<IQueryable<T>, IOrderedQueryable<T>>? orderBy,
        int page,
        int size,
        CancellationToken cancellationToken = default);

    Task<IReadOnlyList<T>> GetAllAsync(CancellationToken cancellationToken = default);
}

public interface IIssueRepository : IEntityRepository<Issue>
{
}
