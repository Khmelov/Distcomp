using lab1.Infrastructure;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

/// <summary>
/// Cache-aside для Notice: после первого успешного GetById данные берутся из Redis.
/// Обновления через REST модуля discussion в Cassandra кеш publisher не сбрасывает
/// (внешний поток не затрагивает ключ — соответствуют сценарии проверки REST+Redis+Kafka).
/// </summary>
public sealed class CachingNoticeService : INoticeService
{
    private readonly NoticeKafkaService _inner;
    private readonly IRedisJsonCache _cache;
    private readonly ILogger<CachingNoticeService> _logger;

    public CachingNoticeService(
        NoticeKafkaService inner,
        IRedisJsonCache cache,
        ILogger<CachingNoticeService> logger)
    {
        _inner = inner;
        _cache = cache;
        _logger = logger;
    }

    public Task<NoticeResponseTo> CreateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        // Не кешируем ответ Create (PENDING); первый GET прогреет кеш согласованным состоянием из Cassandra.
        return _inner.CreateAsync(request, cancellationToken);
    }

    public async Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var key = PublisherCacheKeys.NoticeById(id);
        var cached = await _cache.GetAsync<NoticeResponseTo>(key, cancellationToken).ConfigureAwait(false);
        if (cached != null)
        {
            _logger.LogDebug("Notice {Id} cache hit", id);
            return cached;
        }

        var fresh = await _inner.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(key, fresh, cancellationToken).ConfigureAwait(false);
        return fresh;
    }

    public Task<PageResponseTo<NoticeResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? issueId,
        CancellationToken cancellationToken = default)
        => _inner.GetPageAsync(page, size, sort, issueId, cancellationToken);

    public Task<IReadOnlyList<NoticeResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
        => _inner.GetAllAsync(cancellationToken);

    public async Task<NoticeResponseTo> UpdateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        var updated = await _inner.UpdateAsync(request, cancellationToken).ConfigureAwait(false);
        await _cache.SetAsync(PublisherCacheKeys.NoticeById(updated.Id), updated, cancellationToken).ConfigureAwait(false);
        return updated;
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        await _inner.DeleteAsync(id, cancellationToken).ConfigureAwait(false);
        await _cache.RemoveAsync(PublisherCacheKeys.NoticeById(id), cancellationToken).ConfigureAwait(false);
    }

    public Task<PageResponseTo<NoticeResponseTo>> GetPageByIssueAsync(
        long issueId,
        int page,
        int size,
        string? sort,
        CancellationToken cancellationToken = default)
        => _inner.GetPageByIssueAsync(issueId, page, size, sort, cancellationToken);
}
