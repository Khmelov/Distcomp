using Domain.Models;

namespace Application.Service;

public interface IService<TResponseTo, in TRequestTo>
{
    public Task<IList<TResponseTo>> GetAllAsync();
    public Task<TResponseTo> GetByIdAsync(long id);
    
    public Task<TResponseTo> AddAsync(TRequestTo item);
    
    public Task<TResponseTo?> UpdateAsync(long id, TRequestTo item);
    
    public Task<int> DeleteAsync(long id);
}