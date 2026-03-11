using RestApiTask.Models.Entities;

namespace RestApiTask.Repositories
{
    public interface IRepository<T> where T : class, IHasId
    {
        Task<IEnumerable<T>> GetAllAsync();
        Task<T?> GetByIdAsync(long id);
        Task<T> AddAsync(T entity);
        Task<T> UpdateAsync(T entity);
        Task<bool> DeleteAsync(long id);
    }
}
