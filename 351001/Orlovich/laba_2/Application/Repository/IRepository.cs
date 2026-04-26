using Domain.Models;

namespace Application.Repository;

public interface IRepository<T>
{
    public Task<IList<T>> GetAllAsync();

    public Task<T?> GetByIdAsync(long id);
    
    public Task AddAsync(T editor);
    
    public Task<T?> UpdateAsync(long id,T editor);
    
    public Task<int> DeleteAsync(long id);
}