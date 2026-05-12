using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;

namespace lab1.Services.Interfaces;

public interface INoticeService
{
    Task<NoticeResponseTo> CreateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default);

    Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<PageResponseTo<NoticeResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? issueId,
        CancellationToken cancellationToken = default);

    Task<IReadOnlyList<NoticeResponseTo>> GetAllAsync(CancellationToken cancellationToken = default);

    Task<NoticeResponseTo> UpdateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default);

    Task DeleteAsync(long id, CancellationToken cancellationToken = default);

    Task<PageResponseTo<NoticeResponseTo>> GetPageByIssueAsync(
        long issueId,
        int page,
        int size,
        string? sort,
        CancellationToken cancellationToken = default);
}
