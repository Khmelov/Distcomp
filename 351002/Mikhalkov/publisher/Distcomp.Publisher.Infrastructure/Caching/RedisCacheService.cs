using Microsoft.EntityFrameworkCore.Storage;
using StackExchange.Redis;
using System.Text.Json;

namespace Distcomp.Infrastructure.Caching
{
    public class RedisCacheService
    {
        private readonly StackExchange.Redis.IDatabase _db;

        public RedisCacheService(IConnectionMultiplexer redis)
        {
            _db = redis.GetDatabase();
        }

        public async Task SetAsync<T>(string key, T value)
        {
            string json;
            if (value is string s) json = s; 
            else json = JsonSerializer.Serialize(value, new JsonSerializerOptions { PropertyNameCaseInsensitive = true, PropertyNamingPolicy = JsonNamingPolicy.CamelCase });

            await _db.StringSetAsync(key, json, TimeSpan.FromMinutes(5));
        }

        public async Task<string?> GetAsync(string key)
        {
            return await _db.StringGetAsync(key);
        }

        public async Task RemoveAsync(string key)
        {
            await _db.KeyDeleteAsync(key);
        }
    }
}