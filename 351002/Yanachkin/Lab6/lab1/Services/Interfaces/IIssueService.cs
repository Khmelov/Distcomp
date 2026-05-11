using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;

namespace lab1.Services.Interfaces;

public interface IIssueService
{
    Task<IssueResponseTo> CreateAsync(IssueRequestTo request, CancellationToken cancellationToken = default);

    Task<IssueResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<PageResponseTo<IssueResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? editorId,
        string? titleContains,
        CancellationToken cancellationToken = default);

    Task<IReadOnlyList<IssueResponseTo>> GetAllAsync(CancellationToken cancellationToken = default);

    Task<IssueResponseTo> UpdateAsync(IssueRequestTo request, CancellationToken cancellationToken = default);

    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
}
