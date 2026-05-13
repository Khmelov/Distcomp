using System.Text.Json;
using Microsoft.Extensions.Options;
using StackExchange.Redis;

namespace RW.Discussion.Caching;

public class RedisCacheService : ICacheService
{
    private readonly IConnectionMultiplexer _redis;
    private readonly RedisSettings _settings;
    private readonly ILogger<RedisCacheService> _logger;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public RedisCacheService(
        IConnectionMultiplexer redis,
        IOptions<RedisSettings> settings,
        ILogger<RedisCacheService> logger)
    {
        _redis = redis;
        _settings = settings.Value;
        _logger = logger;
    }

    private string Prefix(string key) => $"{_settings.KeyPrefix}:{key}";

    public async Task<T?> GetAsync<T>(string key) where T : class
    {
        try
        {
            var db = _redis.GetDatabase();
            var value = await db.StringGetAsync(Prefix(key));
            if (value.IsNullOrEmpty) return null;
            return JsonSerializer.Deserialize<T>(value!, JsonOptions);
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Redis GET failed for key {Key}", key);
            return null;
        }
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? ttl = null) where T : class
    {
        try
        {
            var db = _redis.GetDatabase();
            var json = JsonSerializer.Serialize(value, JsonOptions);
            var expiry = ttl ?? TimeSpan.FromSeconds(_settings.TtlSeconds);
            await db.StringSetAsync(Prefix(key), json, expiry);
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Redis SET failed for key {Key}", key);
        }
    }

    public async Task RemoveAsync(string key)
    {
        try
        {
            var db = _redis.GetDatabase();
            await db.KeyDeleteAsync(Prefix(key));
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Redis DEL failed for key {Key}", key);
        }
    }
}
