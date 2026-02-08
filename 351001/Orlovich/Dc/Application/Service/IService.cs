using Domain.Models;

namespace Application.Service;

public interface IService
{
    public Task<IList<EditorResponseTo>> GetAllEditorsAsync();
    public Task<EditorResponseTo> GetEditorByIdAsync(long id);
    
    public Task<EditorResponseTo> AddEditorAsync(EditorRequestTo editor);
    
    public Task<EditorResponseTo?> UpdateEditorAsync(long id,EditorRequestTo editor);
    
    public Task<int> DeleteEditorAsync(long id);
}