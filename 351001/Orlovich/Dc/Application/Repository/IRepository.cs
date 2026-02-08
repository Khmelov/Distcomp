using Domain.Models;

namespace Application.Repository;

public interface IRepository<T>
{
    public Task<IList<Editor>> GetAllAsync();
    public Task<Editor> GetByIdAsync(long id);
    
    public Task AddAsync(T editor);
    
    public Task<Editor?> UpdateAsync(long id,T editor);
    
    public Task<int> DeleteAsync(long id);
}