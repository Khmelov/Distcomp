using lab1.Infrastructure;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

public sealed class CachingIssueService : IIssueService
{
    private readonly IssueService _inner;
    private readonly IRedisJsonCache _cache;

    public CachingIssueService(IssueService inner, IRedisJsonCache cache)
    {
        _inner = inner;
        _cache = cache;
    }

    public async Task<IssueResponseTo> CreateAsync(IssueRequestTo request, CancellationToken cancellationToken = default)
    {
        var created = await _inner.CreateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.IssueById(created.Id), created, cancellationToken).ConfigureAwait(false);
        return created;
    }

    public async Task<IssueResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var key = PublisherCacheKeys.IssueById(id);
        var cached = await _cache.GetAsync<IssueResponseTo>(key, cancellationToken).ConfigureAwait(false);
        if (cached != null)
            return cached;

        var fresh = await _inner.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(key, fresh, cancellationToken).ConfigureAwait(false);
        return fresh;
    }

    public Task<PageResponseTo<IssueResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? editorId,
        string? titleContains,
        CancellationToken cancellationToken = default)
        => _inner.GetPageAsync(page, size, sort, editorId, titleContains, cancellationToken);

    public Task<IReadOnlyList<IssueResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
        => _inner.GetAllAsync(cancellationToken);

    public async Task<IssueResponseTo> UpdateAsync(IssueRequestTo request, CancellationToken cancellationToken = default)
    {
        var updated = await _inner.UpdateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.IssueById(updated.Id), updated, cancellationToken).ConfigureAwait(false);
        return updated;
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        await _inner.DeleteAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.RemoveAsync(PublisherCacheKeys.IssueById(id), cancellationToken).ConfigureAwait(false);
    }
}
