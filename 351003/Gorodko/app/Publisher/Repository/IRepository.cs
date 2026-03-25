using Publisher.Model;
using Publisher.Repository.Params;

namespace Publisher.Repository {
    public interface IRepository<T> where T : BaseEntity {
        Task<T?> GetByIdAsync(long id);
        Task<IEnumerable<T>> GetAllAsync();
        Task<T> AddAsync(T entity);
        Task<T> UpdateAsync(T entity);
        Task<bool> DeleteAsync(long id);
        Task<bool> ExistsAsync(long id);

        Task<PagedResponse<T>> GetPagedAsync(QueryParams queryParams);
        Task<IEnumerable<T>> FindAsync(FilterCriteria<T> filter);
        Task<IEnumerable<T>> GetSortedAsync(string sortBy, string sortOrder = "asc");
        string GetConnectionString();
    }
}