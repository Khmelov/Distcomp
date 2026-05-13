using System.Text.Json;
using StackExchange.Redis;

namespace lab1.Infrastructure;

/// <summary>Сериализация DTO в Redis в формате camelCase, как в REST/Kafka.</summary>
public sealed class RedisJsonCache : IRedisJsonCache
{
    private readonly IConnectionMultiplexer _mux;
    private readonly ILogger<RedisJsonCache> _logger;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        PropertyNameCaseInsensitive = true
    };

    public RedisJsonCache(IConnectionMultiplexer mux, ILogger<RedisJsonCache> logger)
    {
        _mux = mux;
        _logger = logger;
    }

    public async Task<T?> GetAsync<T>(string key, CancellationToken cancellationToken = default) where T : class
    {
        try
        {
            var val = await _mux.GetDatabase().StringGetAsync(key).ConfigureAwait(false);
            if (!val.HasValue)
                return null;

            return JsonSerializer.Deserialize<T>(val!, JsonOptions);
        }
        catch (RedisException ex)
        {
            _logger.LogWarning(ex, "Redis GET {Key} failed; cache miss", key);
            return null;
        }
    }

    public async Task SetAsync<T>(string key, T value, CancellationToken cancellationToken = default) where T : class
    {
        try
        {
            var json = JsonSerializer.Serialize(value, JsonOptions);
            await _mux.GetDatabase().StringSetAsync(key, json).ConfigureAwait(false);
        }
        catch (RedisException ex)
        {
            _logger.LogWarning(ex, "Redis SET {Key} failed", key);
        }
    }

    public async Task RemoveAsync(string key, CancellationToken cancellationToken = default)
    {
        try
        {
            await _mux.GetDatabase().KeyDeleteAsync(key).ConfigureAwait(false);
        }
        catch (RedisException ex)
        {
            _logger.LogWarning(ex, "Redis DEL {Key} failed", key);
        }
    }
}
