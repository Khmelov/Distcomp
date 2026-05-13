using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;

namespace lab1.Services.Interfaces;

public interface IEditorService
{
    Task<EditorResponseTo> CreateAsync(EditorRequestTo request, CancellationToken cancellationToken = default);

    Task<EditorResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<PageResponseTo<EditorResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? loginContains,
        CancellationToken cancellationToken = default);

    Task<IReadOnlyList<EditorResponseTo>> GetAllAsync(CancellationToken cancellationToken = default);

    Task<EditorResponseTo> UpdateAsync(EditorRequestTo request, CancellationToken cancellationToken = default);

    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
}
