using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Caching;

public class RedisCacheService : IRedisCacheService
{
    private readonly IDistributedCache _cache;
    private readonly ILogger<RedisCacheService> _logger;
    private readonly JsonSerializerOptions _jsonOptions;

    public RedisCacheService(IDistributedCache cache, ILogger<RedisCacheService> logger)
    {
        _cache = cache;
        _logger = logger;
        _jsonOptions = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true,
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase
        };
    }

    public async Task<T?> GetAsync<T>(string key)
    {
        try
        {
            var data = await _cache.GetStringAsync(key);
            if (string.IsNullOrEmpty(data))
                return default;

            return JsonSerializer.Deserialize<T>(data, _jsonOptions);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting cache key: {Key}", key);
            return default;
        }
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? expiration = null)
    {
        try
        {
            var options = new DistributedCacheEntryOptions();

            if (expiration.HasValue)
                options.SetAbsoluteExpiration(expiration.Value);
            else
                options.SetAbsoluteExpiration(TimeSpan.FromMinutes(5));

            var data = JsonSerializer.Serialize(value, _jsonOptions);
            await _cache.SetStringAsync(key, data, options);

            _logger.LogDebug("Cache set for key: {Key}", key);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error setting cache key: {Key}", key);
        }
    }

    public async Task RemoveAsync(string key)
    {
        try
        {
            await _cache.RemoveAsync(key);
            _logger.LogDebug("Cache removed for key: {Key}", key);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error removing cache key: {Key}", key);
        }
    }

    public async Task<bool> ExistsAsync(string key)
    {
        try
        {
            var data = await _cache.GetStringAsync(key);
            return data != null;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error checking cache key: {Key}", key);
            return false;
        }
    }

    public async Task<T?> GetOrSetAsync<T>(string key, Func<Task<T>> factory, TimeSpan? expiration = null)
    {
        var cached = await GetAsync<T>(key);
        if (cached != null)
            return cached;

        var result = await factory();
        if (result != null)
            await SetAsync(key, result, expiration);

        return result;
    }

    public async Task RemoveByPatternAsync(string pattern)
    {
        //Для удаления по паттерну нужен доступ к Redis серверу
        //В простой реализации можно обойтись без этого метода
        _logger.LogWarning("RemoveByPatternAsync not fully implemented");
        await Task.CompletedTask;
    }
}