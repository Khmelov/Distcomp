// File: RedisCacheService.cs
using StackExchange.Redis;
using System.Text.Json;

public class RedisCacheService : ICacheService
{
    private readonly IConnectionMultiplexer _redis;
    private readonly IDatabase _db;

    public RedisCacheService(IConnectionMultiplexer redis)
    {
        _redis = redis;
        _db = redis.GetDatabase();
    }

    public async Task<string?> GetAsync(string key)
    {
        var value = await _db.StringGetAsync(key);
        return value.HasValue ? value.ToString() : null;
    }

    public async Task SetAsync(string key, string value, TimeSpan? expiry = null)
    {
        await _db.StringSetAsync(key, value, expiry);
    }

    public async Task RemoveAsync(string key)
    {
        await _db.KeyDeleteAsync(key);
    }

    public async Task<T?> GetObjectAsync<T>(string key) where T : class
    {
        var json = await GetAsync(key);
        return json == null ? null : JsonSerializer.Deserialize<T>(json);
    }

    public async Task SetObjectAsync<T>(string key, T obj, TimeSpan? expiry = null) where T : class
    {
        var json = JsonSerializer.Serialize(obj);
        await SetAsync(key, json, expiry);
    }
}