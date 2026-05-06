using StackExchange.Redis;
using System.Text.Json;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services;

public class RedisCacheService : ICacheService
{
    private readonly IDatabase _db;
    private readonly IServer _server;
    private readonly string _instanceName;
    private static readonly TimeSpan DefaultExpiration = TimeSpan.FromMinutes(30);

    public RedisCacheService(IConnectionMultiplexer connection, string instanceName = "cache:")
    {
        _db = connection.GetDatabase();
        _server = connection.GetServer(connection.GetEndPoints().First());
        _instanceName = instanceName;
    }

    public async Task<T?> GetAsync<T>(string key)
    {
        var value = await _db.StringGetAsync(_instanceName + key);
        if (!value.HasValue)
            return default;

        return JsonSerializer.Deserialize<T>(value.ToString());
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? expiration = null)
    {
        var json = JsonSerializer.Serialize(value);
        var finalExpiration = expiration ?? DefaultExpiration;
        await _db.StringSetAsync(_instanceName + key, json, finalExpiration);
    }

    public async Task RemoveAsync(string key)
    {
        await _db.KeyDeleteAsync(_instanceName + key);
    }

    public async Task RemoveByPatternAsync(string pattern)
    {
        var keys = _server.Keys(database: _db.Database, pattern: _instanceName + pattern).ToArray();
        if (keys.Length > 0)
        {
            await _db.KeyDeleteAsync(keys);
        }
    }
}
