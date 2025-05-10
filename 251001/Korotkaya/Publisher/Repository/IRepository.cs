using System;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace WebApplication1.Repository
{
    public interface IRepository<T> where T : class
    {
        Task<PaginatedResult<T>> GetAllAsync(
            int pageNumber,
            int pageSize,
            Expression<Func<T, bool>>? filter = null,
            Func<IQueryable<T>, IOrderedQueryable<T>>? orderBy = null);
        Task<T?> GetByIdAsync(long id);
        Task<T> CreateAsync(T entity);
        Task<T> UpdateAsync(T entity);
        Task DeleteAsync(long id);
        Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate);
    }
}
