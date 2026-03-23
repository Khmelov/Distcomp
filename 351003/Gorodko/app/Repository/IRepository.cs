using Project.Model;

namespace Project.Repository {
    public interface IRepository<T> where T : BaseEntity {
        Task<T?> GetByIdAsync(long id);
        Task<IEnumerable<T>> GetAllAsync();
        Task<T> AddAsync(T entity);
        Task<T> UpdateAsync(T entity);
        Task<bool> DeleteAsync(long id);
        Task<bool> ExistsAsync(long id);
    }
}