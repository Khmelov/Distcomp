using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;

namespace Redis.Services;

public class RedisCacheService : ICacheService
{
    private readonly IDistributedCache _cache;

    public RedisCacheService(IDistributedCache cache)
    {
        _cache = cache;
    }

    public async Task<T?> GetAsync<T>(string key)
    {
        var value = await _cache.GetStringAsync(key);
        return value == null ? default : JsonSerializer.Deserialize<T>(value);
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? expirationTime = null)
    {
        var options = new DistributedCacheEntryOptions();
        if (expirationTime.HasValue)
        {
            options.AbsoluteExpirationRelativeToNow = expirationTime.Value;
        }

        await _cache.SetStringAsync(key, JsonSerializer.Serialize(value), options);
    }
}