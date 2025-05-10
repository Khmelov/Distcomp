// CachedStickerRepository.cs

using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class CachedStickerRepository : IStickerRepository
{
    private readonly IStickerRepository _decorated;
    private readonly IDistributedCache _cache;
    private readonly TimeSpan _cacheDuration = TimeSpan.FromMinutes(2);

    public CachedStickerRepository(IStickerRepository decorated, IDistributedCache cache)
    {
        _decorated = decorated;
        _cache = cache;
    }

    public async Task<IEnumerable<Sticker>> GetAllAsync()
    {
        const string cacheKey = "stickers_all";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
            return JsonSerializer.Deserialize<IEnumerable<Sticker>>(cachedData);

        var stickers = await _decorated.GetAllAsync();
        await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(stickers), new DistributedCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = _cacheDuration
        });
        
        return stickers;
    }

    public async Task<Sticker?> GetByIdAsync(long id)
    {
        var cacheKey = $"sticker_{id}";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        
        if (!string.IsNullOrEmpty(cachedData))
            return JsonSerializer.Deserialize<Sticker>(cachedData);

        var sticker = await _decorated.GetByIdAsync(id);
        if (sticker != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(sticker), new DistributedCacheEntryOptions
            {
                AbsoluteExpirationRelativeToNow = _cacheDuration
            });
        }
        
        return sticker;
    }

    public async Task<Sticker> CreateAsync(Sticker entity)
    {
        var result = await _decorated.CreateAsync(entity);
        await InvalidateCacheForTag(result.Id);
        return result;
    }

    public async Task<Sticker?> UpdateAsync(Sticker entity)
    {
        var result = await _decorated.UpdateAsync(entity);
        if (result != null)
            await InvalidateCacheForTag(result.Id);
        
        return result;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var result = await _decorated.DeleteAsync(id);
        if (result)
            await InvalidateCacheForTag(id);
        
        return result;
    }

    private async Task InvalidateCacheForTag(long tagId)
    {
        await _cache.RemoveAsync($"sticker_{tagId}");
        await _cache.RemoveAsync("stickers_all");
    }
}