using lab1.Infrastructure;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

public sealed class CachingLabelService : ILabelService
{
    private readonly LabelService _inner;
    private readonly IRedisJsonCache _cache;

    public CachingLabelService(LabelService inner, IRedisJsonCache cache)
    {
        _inner = inner;
        _cache = cache;
    }

    public async Task<LabelResponseTo> CreateAsync(LabelRequestTo request, CancellationToken cancellationToken = default)
    {
        var created = await _inner.CreateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.LabelById(created.Id), created, cancellationToken).ConfigureAwait(false);
        return created;
    }

    public async Task<LabelResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var key = PublisherCacheKeys.LabelById(id);
        var cached = await _cache.GetAsync<LabelResponseTo>(key, cancellationToken).ConfigureAwait(false);
        if (cached != null)
            return cached;

        var fresh = await _inner.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(key, fresh, cancellationToken).ConfigureAwait(false);
        return fresh;
    }

    public Task<PageResponseTo<LabelResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? nameContains,
        CancellationToken cancellationToken = default)
        => _inner.GetPageAsync(page, size, sort, nameContains, cancellationToken);

    public Task<IReadOnlyList<LabelResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
        => _inner.GetAllAsync(cancellationToken);

    public async Task<LabelResponseTo> UpdateAsync(LabelRequestTo request, CancellationToken cancellationToken = default)
    {
        var updated = await _inner.UpdateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.LabelById(updated.Id), updated, cancellationToken).ConfigureAwait(false);
        return updated;
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        await _inner.DeleteAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.RemoveAsync(PublisherCacheKeys.LabelById(id), cancellationToken).ConfigureAwait(false);
    }
}
