public interface ICacheService
{
    Task<string?> GetAsync(string key);
    Task SetAsync(string key, string value, TimeSpan? expiry = null);
    Task RemoveAsync(string key);
    Task<T?> GetObjectAsync<T>(string key) where T : class;
    Task SetObjectAsync<T>(string key, T obj, TimeSpan? expiry = null) where T : class;
}