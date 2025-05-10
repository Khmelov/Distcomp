using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class CachedAuthorRepository : IAuthorRepository
{
    private readonly IAuthorRepository _decorated;
    private readonly IDistributedCache _cache;
    private readonly TimeSpan _cacheDuration = TimeSpan.FromMinutes(2);

    public CachedAuthorRepository(IAuthorRepository decorated, IDistributedCache cache)
    {
        _decorated = decorated;
        _cache = cache;
    }

    public async Task<IEnumerable<Author>> GetAllAsync()
    {
        const string cacheKey = "authors_all";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<IEnumerable<Author>>(cachedData);
        }

        var authors = await _decorated.GetAllAsync();
        await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(authors), new DistributedCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = _cacheDuration
        });
        
        return authors;
    }

    public async Task<Author?> GetByIdAsync(long id)
    {
        string cacheKey = $"author_{id}";
        Console.WriteLine(cacheKey);
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<Author>(cachedData);
        }

        var author = await _decorated.GetByIdAsync(id);
        if (author != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(author), new DistributedCacheEntryOptions
            {
                AbsoluteExpirationRelativeToNow = _cacheDuration
            });
        }
        
        return author;
    }

    // Остальные методы с инвалидацией кэша
    public async Task<Author> CreateAsync(Author entity)
    {
        var result = await _decorated.CreateAsync(entity);
        await InvalidateCacheForUser(result.Id);
        return result;
    }

    public async Task<Author?> UpdateAsync(Author entity)
    {
        var result = await _decorated.UpdateAsync(entity);
        if (result != null)
        {
            await InvalidateCacheForUser(result.Id);
        }
        return result;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var result = await _decorated.DeleteAsync(id);
        if (result)
        {
            await InvalidateCacheForUser(id);
        }
        return result;
    }

    private async Task InvalidateCacheForUser(long userId)
    {
        await _cache.RemoveAsync($"author_{userId}");
        await _cache.RemoveAsync("authors_all");
    }
}