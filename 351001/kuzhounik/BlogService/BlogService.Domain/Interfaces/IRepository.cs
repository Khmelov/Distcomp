namespace BlogService.Domain.Interfaces;

public interface IRepository<ID, T> where T : class
{
    Task<T?> GetByIdAsync(ID id);
    Task<IEnumerable<T>> GetAllAsync();
    Task AddAsync(T entity);
    Task UpdateAsync(T entity);
    Task DeleteAsync(ID id);
}