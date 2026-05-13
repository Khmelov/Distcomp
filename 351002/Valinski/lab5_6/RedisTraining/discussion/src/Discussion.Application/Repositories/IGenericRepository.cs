namespace Discussion.Application.Repositories;

public interface IGenericRepository<T>
{
    Task<T?> GetByIdAsync(long id);
    Task<List<T>> GetAllAsync();
    Task AddAsync(T entity);
    Task<T> UpdateAsync(T entity);
    Task DeleteAsync(T entity);
}
