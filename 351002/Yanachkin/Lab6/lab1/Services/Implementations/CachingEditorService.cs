using lab1.Infrastructure;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

public sealed class CachingEditorService : IEditorService
{
    private readonly EditorService _inner;
    private readonly IRedisJsonCache _cache;

    public CachingEditorService(EditorService inner, IRedisJsonCache cache)
    {
        _inner = inner;
        _cache = cache;
    }

    public async Task<EditorResponseTo> CreateAsync(EditorRequestTo request, CancellationToken cancellationToken = default)
    {
        var created = await _inner.CreateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.EditorById(created.Id), created, cancellationToken).ConfigureAwait(false);
        return created;
    }

    public async Task<EditorResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var key = PublisherCacheKeys.EditorById(id);
        var cached = await _cache.GetAsync<EditorResponseTo>(key, cancellationToken).ConfigureAwait(false);
        if (cached != null)
            return cached;

        var fresh = await _inner.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(key, fresh, cancellationToken).ConfigureAwait(false);
        return fresh;
    }

    public Task<PageResponseTo<EditorResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? loginContains,
        CancellationToken cancellationToken = default)
        => _inner.GetPageAsync(page, size, sort, loginContains, cancellationToken);

    public Task<IReadOnlyList<EditorResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
        => _inner.GetAllAsync(cancellationToken);

    public async Task<EditorResponseTo> UpdateAsync(EditorRequestTo request, CancellationToken cancellationToken = default)
    {
        var updated = await _inner.UpdateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.EditorById(updated.Id), updated, cancellationToken).ConfigureAwait(false);
        return updated;
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        await _inner.DeleteAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.RemoveAsync(PublisherCacheKeys.EditorById(id), cancellationToken).ConfigureAwait(false);
    }
}
