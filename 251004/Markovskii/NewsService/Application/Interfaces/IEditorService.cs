using Application.DTO.Request;
using Application.DTO.Request.Editor;
using Application.DTO.Response;
using Application.DTO.Response.Editor;

namespace Application.Interfaces;

public interface IEditorService
{
    public Task<EditorResponseToGetById?> CreateEditor(EditorRequestToCreate model);
    public Task<IEnumerable<EditorResponseToGetById?>?> GetEditors(EditorRequestToGetAll request);
    public Task<EditorResponseToGetById?> GetEditorById(EditorRequestToGetById request);
    public Task<EditorResponseToGetById?> GetEditorByNewsId(EditorRequestToGetByNewsId request);
    public Task<EditorResponseToGetById?> UpdateEditor(EditorRequestToFullUpdate model);
    public Task<EditorResponseToGetById?> DeleteEditor(EditorRequestToDeleteById request);
}