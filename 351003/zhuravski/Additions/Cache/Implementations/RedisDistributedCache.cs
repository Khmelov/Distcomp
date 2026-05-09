using System.Text.Json;
using Additions.Cache.Interfaces;
using Microsoft.Extensions.Logging;
using StackExchange.Redis;

namespace Additions.Cache.Implementations;

public class RedisDistributedCache : IDistributedCache
{
    private readonly ILogger<RedisDistributedCache> logger;
    private readonly IConnectionMultiplexer connection;
    private readonly IDatabase db;
    private bool isDisposed = false;

    public RedisDistributedCache(IConnectionMultiplexer connection,
                                 ILogger<RedisDistributedCache> logger)
    {
        this.connection = connection;
        db = connection.GetDatabase();
        this.logger = logger;
    }

    public async Task<T?> GetAsync<T>(string key, CancellationToken ct = default)
    {
        try
        {
            RedisValue value = await db.StringGetAsync(key);
            if (value.IsNullOrEmpty)
            {
                return default;
            }
            else {
                return JsonSerializer.Deserialize<T>(value!.ToString());
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Cache miss for key: {Key}", key);
            return default;
        }
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? ttl = null, CancellationToken ct = default)
    {
        try
        {
            var serialized = JsonSerializer.Serialize(value);
            await db.StringSetAsync(key, serialized, ttl ?? TimeSpan.MaxValue);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to cache key: {Key}", key);
        }
    }

    public async Task RemoveAsync(string key, CancellationToken ct = default)
    {
        try
        {
            await db.KeyDeleteAsync(key);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to remove key: {Key}", key);
        }
    }

    public async Task<bool> ExistsAsync(string key, CancellationToken ct = default)
    {
        try
        {
            return await db.KeyExistsAsync(key);
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Failed to check key existence: {Key}", key);
            return false;
        }
    }

    public async Task<T> GetOrSetAsync<T>(string key, Func<Task<T>> factory, 
                                            TimeSpan? ttl = null, CancellationToken ct = default)
    {
        var cached = await GetAsync<T>(key, ct);
        if (cached != null)
        {
            logger.LogDebug("Cache hit for key: {Key}", key);
            return cached;
        }
        logger.LogDebug("Cache miss for key: {Key}", key);
        var value = await factory();
        await SetAsync(key, value, ttl, ct);
        return value;
    }

    public async Task RemoveByPatternAsync(string pattern, CancellationToken ct = default)
    {
        try
        {
            var endpoints = connection.GetEndPoints();
            foreach (var endpoint in endpoints)
            {
                var server = connection.GetServer(endpoint);
                var keys = server.Keys(pattern: pattern);
                foreach (var key in keys)
                {
                    await db.KeyDeleteAsync(key);
                }
            }
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to remove keys by pattern: {Pattern}", pattern);
        }
    }

    public async Task<long> IncrementAsync(string key, long delta = 1, CancellationToken ct = default)
    {
        try
        {
            return await db.StringIncrementAsync(key, delta);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to increment key: {Key}", key);
            return 0;
        }
    }

    public void Dispose()
    {
        if (!isDisposed)
        {
            isDisposed = true;
            GC.SuppressFinalize(this);
        }
    }
}