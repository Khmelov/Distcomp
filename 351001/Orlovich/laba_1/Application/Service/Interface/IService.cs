using Domain.Models;

namespace Application.Service;

public interface IService<TResponseTo, in TRequestTo>
{
    public Task<IList<TResponseTo>> GetAllEditorsAsync();
    public Task<TResponseTo> GetEditorByIdAsync(long id);
    
    public Task<TResponseTo> AddEditorAsync(TRequestTo editor);
    
    public Task<TResponseTo?> UpdateEditorAsync(long id, TRequestTo editor);
    
    public Task<int> DeleteEditorAsync(long id);
}