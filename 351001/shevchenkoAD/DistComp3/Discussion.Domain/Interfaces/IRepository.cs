using System.Linq.Expressions;
using Discussion.Domain.Abstractions;
using Discussion.Domain.Entities;

namespace Discussion.Domain.Interfaces;

public interface IRepository<T> where T : BaseEntity {
    Task<IEnumerable<T>> GetAllAsync();

    Task<T?> GetByIdAsync(long id);

    Task<T> CreateAsync(T entity);

    Task<T?> UpdateAsync(T entity);

    Task<bool> DeleteAsync(long id);
}

public interface ICommentRepository : IRepository<Comment>
{
    Task<IEnumerable<Comment>> GetByIssueIdAsync(long issueId);

    Task DeleteByIssueIdAsync(long issueId);
}