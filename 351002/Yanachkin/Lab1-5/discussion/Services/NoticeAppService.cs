using discussion.Infrastructure;
using discussion.Models.DTO.Requests;
using discussion.Models.DTO.Responses;
using discussion.Models.Domain;
using discussion.Repositories;

namespace discussion.Services;

public interface INoticeAppService
{
    Task<NoticeResponseTo> CreateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default);
    Task<NoticeResponseTo> CreateWithAssignedIdAsync(long id, NoticeRequestTo request,
        CancellationToken cancellationToken = default);
    Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default);
    Task<PageResponseTo<NoticeResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? issueId,
        CancellationToken cancellationToken = default);
    Task<IReadOnlyList<NoticeResponseTo>> GetAllAsync(string? sort, CancellationToken cancellationToken = default);
    Task<NoticeResponseTo> UpdateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default);
    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
    Task DeleteAllForIssueAsync(long issueId, CancellationToken cancellationToken = default);
    Task<PageResponseTo<NoticeResponseTo>> GetPageByIssueAsync(
        long issueId,
        int page,
        int size,
        string? sort,
        CancellationToken cancellationToken = default);
}

public class NoticeAppService : INoticeAppService
{
    private readonly INoticeRepository _repository;

    public NoticeAppService(INoticeRepository repository)
    {
        _repository = repository;
    }

    public async Task<NoticeResponseTo> CreateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        var id = NoticeIdGenerator.NextId();
        var trimmed = request.Content.Trim();
        var state = NoticeModerator.Evaluate(trimmed);
        var entity = new NoticeEntity(id, request.IssueId, trimmed, state);
        await _repository.InsertAsync(entity, cancellationToken).ConfigureAwait(false);
        return ToDto(entity);
    }

    public async Task<NoticeResponseTo> CreateWithAssignedIdAsync(long id, NoticeRequestTo request,
        CancellationToken cancellationToken = default)
    {
        Validate(request);
        if (id <= 0)
            throw new ArgumentException("Id must be positive");

        var trimmed = request.Content.Trim();
        var state = NoticeModerator.Evaluate(trimmed);
        var entity = new NoticeEntity(id, request.IssueId, trimmed, state);
        var existing = await _repository.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        if (existing != null)
        {
            await _repository.UpdateAsync(existing, entity, cancellationToken).ConfigureAwait(false);
            return ToDto(entity);
        }

        await _repository.InsertAsync(entity, cancellationToken).ConfigureAwait(false);
        return ToDto(entity);
    }

    public async Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var notice = await _repository.GetByIdAsync(id, cancellationToken).ConfigureAwait(false)
            ?? throw new KeyNotFoundException("Notice not found");
        return ToDto(notice);
    }

    public async Task<PageResponseTo<NoticeResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? issueId,
        CancellationToken cancellationToken = default)
    {
        var (safePage, safeSize) = NormalizePage(page, size);
        IReadOnlyList<NoticeEntity> raw = issueId.HasValue
            ? await _repository.GetByIssueIdAsync(issueId.Value, cancellationToken).ConfigureAwait(false)
            : await _repository.GetAllFromBucketsAsync(cancellationToken).ConfigureAwait(false);

        var ordered = raw.ToList();
        ordered.Sort(NoticeSortComparer.For(sort));

        var total = ordered.Count;
        var slice = ordered.Skip(safePage * safeSize).Take(safeSize).Select(ToDto).ToList();
        var pages = safeSize == 0 ? 0 : (int)Math.Ceiling(total / (double)safeSize);

        return new PageResponseTo<NoticeResponseTo>
        {
            Content = slice,
            TotalElements = total,
            TotalPages = pages,
            Number = safePage,
            Size = safeSize
        };
    }

    public async Task<IReadOnlyList<NoticeResponseTo>> GetAllAsync(string? sort, CancellationToken cancellationToken = default)
    {
        var raw = await _repository.GetAllFromBucketsAsync(cancellationToken).ConfigureAwait(false);
        var list = raw.ToList();
        list.Sort(NoticeSortComparer.For(sort));
        return list.Select(ToDto).ToList();
    }

    public async Task<NoticeResponseTo> UpdateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        if (request.Id <= 0)
            throw new ArgumentException("Id must be positive for update");

        var existing = await _repository.GetByIdAsync(request.Id, cancellationToken).ConfigureAwait(false)
            ?? throw new KeyNotFoundException("Notice not found");

        var trimmed = request.Content.Trim();
        var state = NoticeModerator.Evaluate(trimmed);
        var updated = new NoticeEntity(request.Id, request.IssueId, trimmed, state);
        await _repository.UpdateAsync(existing, updated, cancellationToken).ConfigureAwait(false);
        return ToDto(updated);
    }

    public Task DeleteAsync(long id, CancellationToken cancellationToken = default)
        => _repository.DeleteAsync(id, cancellationToken);

    public Task DeleteAllForIssueAsync(long issueId, CancellationToken cancellationToken = default)
        => _repository.DeleteAllByIssueIdAsync(issueId, cancellationToken);

    public Task<PageResponseTo<NoticeResponseTo>> GetPageByIssueAsync(
        long issueId,
        int page,
        int size,
        string? sort,
        CancellationToken cancellationToken = default)
        => GetPageAsync(page, size, sort, issueId, cancellationToken);

    private static (int page, int size) NormalizePage(int page, int size)
    {
        var safeSize = size <= 0 ? 20 : Math.Min(size, 200);
        var safePage = page < 0 ? 0 : page;
        return (safePage, safeSize);
    }

    private static void Validate(NoticeRequestTo request)
    {
        if (request.IssueId <= 0)
            throw new ArgumentException("IssueId must be positive");

        if (string.IsNullOrWhiteSpace(request.Content))
            throw new ArgumentException("Content must not be empty");

        if (request.Content.Trim().Length < 2)
            throw new ArgumentException("Content must be at least 2 characters");
    }

    private static NoticeResponseTo ToDto(NoticeEntity e) =>
        new() { Id = e.Id, IssueId = e.IssueId, Content = e.Content, State = e.State };
}
