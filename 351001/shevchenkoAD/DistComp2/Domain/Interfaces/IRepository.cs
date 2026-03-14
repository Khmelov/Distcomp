using System.Linq.Expressions;
using Domain.Abstractions;
using Domain.Entities;

namespace Domain.Interfaces;

public interface IRepository<T> where T : BaseEntity {
    Task<IEnumerable<T>> GetAllAsync();

    Task<T?> GetByIdAsync(long id);

    Task<T> CreateAsync(T entity);

    Task<T?> UpdateAsync(T entity);

    Task<bool> DeleteAsync(long id);
    
    Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate);
}

public interface IIssueRepository : IRepository<Issue>
{
    Task<Issue?> GetByIdWithLabelsAsync(long id);
    
    Task<bool> IsLabelUsedAsync(long labelId);
}