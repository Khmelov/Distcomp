using WebApplication1.DTO;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public interface IEditorService
    {
        Task<EditorResponseTo> CreateEditorAsync(EditorRequestTo dto);
        Task<EditorResponseTo> GetEditorByIdAsync(long id);
        Task<PaginatedResult<EditorResponseTo>> GetAllEditorsAsync(int pageNumber = 1, int pageSize = 10);
        Task<EditorResponseTo> UpdateEditorAsync(long id, EditorRequestTo dto);
        Task DeleteEditorAsync(long id);
    }
}
