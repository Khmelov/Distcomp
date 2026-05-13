using discussion.Models.Domain;

namespace discussion.Repositories;

public interface INoticeRepository
{
    Task<NoticeEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<NoticeEntity>> GetByIssueIdAsync(long issueId, CancellationToken cancellationToken = default);
    Task<IReadOnlyList<NoticeEntity>> GetAllFromBucketsAsync(CancellationToken cancellationToken = default);
    Task InsertAsync(NoticeEntity entity, CancellationToken cancellationToken = default);
    Task UpdateAsync(NoticeEntity previous, NoticeEntity updated, CancellationToken cancellationToken = default);
    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
    Task DeleteAllByIssueIdAsync(long issueId, CancellationToken cancellationToken = default);
}
