namespace RW.Discussion.Caching;

public interface ICacheService
{
    Task<T?> GetAsync<T>(string key) where T : class;
    Task SetAsync<T>(string key, T value, TimeSpan? ttl = null) where T : class;
    Task RemoveAsync(string key);
}
