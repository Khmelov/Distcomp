// CachedTopicRepository.cs

using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class CachedTopicRepository : ITopicRepository
{
    private readonly ITopicRepository _decorated;
    private readonly IDistributedCache _cache;
    private readonly TimeSpan _cacheDuration = TimeSpan.FromMinutes(2);

    public CachedTopicRepository(ITopicRepository decorated, IDistributedCache cache)
    {
        _decorated = decorated;
        _cache = cache;
    }

    public async Task<IEnumerable<Topic>> GetAllAsync()
    {
        const string cacheKey = "topics_all";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
            return JsonSerializer.Deserialize<IEnumerable<Topic>>(cachedData);

        var topics = await _decorated.GetAllAsync();
        await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(topics), new DistributedCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = _cacheDuration
        });
        
        return topics;
    }

    public async Task<Topic?> GetByIdAsync(long id)
    {
        var cacheKey = $"topic_{id}";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
            return JsonSerializer.Deserialize<Topic>(cachedData);

        var topic = await _decorated.GetByIdAsync(id);
        if (topic != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(topic), new DistributedCacheEntryOptions
            {
                AbsoluteExpirationRelativeToNow = _cacheDuration
            });
        }
        
        return topic;
    }

    public async Task<Topic> CreateAsync(Topic entity)
    {
        var result = await _decorated.CreateAsync(entity);
        await InvalidateCacheForStory(result.Id);
        return result;
    }

    public async Task<Topic?> UpdateAsync(Topic entity)
    {
        var result = await _decorated.UpdateAsync(entity);
        if (result != null)
            await InvalidateCacheForStory(result.Id);
        
        return result;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var result = await _decorated.DeleteAsync(id);
        if (result)
            await InvalidateCacheForStory(id);
        
        return result;
    }

    private async Task InvalidateCacheForStory(long topicId)
    {
        await _cache.RemoveAsync($"topic_{topicId}");
        await _cache.RemoveAsync("topics_all");
    }
}