using Domain.Abstractions;

namespace Domain.Interfaces;

public interface IRepository<T> where T : BaseEntity {
    Task<IEnumerable<T>> GetAllAsync();

    Task<T?> GetByIdAsync(long id);

    Task<T> CreateAsync(T entity);

    Task<T?> UpdateAsync(T entity);

    Task<bool> DeleteAsync(long id);
}