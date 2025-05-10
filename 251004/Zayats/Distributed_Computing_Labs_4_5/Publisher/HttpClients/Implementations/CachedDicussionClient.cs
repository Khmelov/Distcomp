using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;
using Publisher.HttpClients.Interfaces;

namespace Publisher.HttpClients.Implementations;

public class CachedDiscussionClient : IDiscussionClient
{
    private readonly IDiscussionClient _innerClient;
    private readonly IDistributedCache _cache;
    private readonly JsonSerializerOptions _jsonOptions = new() { PropertyNameCaseInsensitive = true };
    private readonly TimeSpan _cacheDuration = TimeSpan.FromMinutes(2);

    public CachedDiscussionClient(IDiscussionClient innerClient, IDistributedCache cache)
    {
        _innerClient = innerClient;
        _cache = cache;
    }
    
    public async Task<IEnumerable<MessageResponseDTO>?> GetNoticesAsync()
    {
        const string cacheKey = "discussion:messages_all";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<IEnumerable<MessageResponseDTO>>(cachedData, _jsonOptions);
        }
        
        var messages = await _innerClient.GetNoticesAsync();
        if (messages != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(messages, _jsonOptions),
                new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = _cacheDuration
                });
        }
        
        return messages;
    }

    public async Task<MessageResponseDTO?> GetNoticeByIdAsync(long id)
    {
        string cacheKey = $"discussion:messages:{id}";
        var cachedData = await _cache.GetStringAsync(cacheKey);
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<MessageResponseDTO>(cachedData, _jsonOptions);
        }
        
        var message = await _innerClient.GetNoticeByIdAsync(id);
        if (message != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(message, _jsonOptions),
                new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = _cacheDuration
                });
        }
        
        return message;
    }

    public async Task<MessageResponseDTO?> CreateNoticeAsync(MessageRequestDTO post)
    {
        var message = await _innerClient.CreateNoticeAsync(post);
        await InvalidateCacheAsync(message.Id);
        return message;
    }

    public async Task<MessageResponseDTO?> UpdateNoticeAsync(MessageRequestDTO post)
    {
        var message = await _innerClient.UpdateNoticeAsync(post);
        await InvalidateCacheAsync(message.Id);
        return message;
    }

    public async Task DeleteNoticeAsync(long id)
    {
        await _innerClient.DeleteNoticeAsync(id);
        await InvalidateCacheAsync(id);
    }

    /// <summary>
    /// Инвалидирует кэш для списка и отдельного Notice.
    /// Если у вас есть дополнительные ключи, их тоже можно добавить.
    /// </summary>
    private async Task InvalidateCacheAsync(long id)
    {
        await _cache.RemoveAsync("discussion:messages_all");
        await _cache.RemoveAsync($"discussion:message:{id}");
    }
}
