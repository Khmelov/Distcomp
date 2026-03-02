using Application.DTOs.Requests;
using Application.DTOs.Responses;

namespace Application.Interfaces
{
    public interface IEditorService
    {
        Task<EditorResponseTo> CreateEditor(EditorRequestTo createEditorRequestTo);

        Task<IEnumerable<EditorResponseTo>> GetAllEditors();

        Task<EditorResponseTo> GetEditor(EditorRequestTo getEditorRequestTo);

        Task<EditorResponseTo> UpdateEditor(EditorRequestTo updateEditorRequestTo);

        Task DeleteEditor(EditorRequestTo deleteEditorRequestTo);
    }
}
