using System.Text.Json;
using Infrastructure.Kafka;
using Microsoft.Extensions.Caching.Distributed;
using BusinessLogic.Servicies;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using DataAccess.Models;
public class PostService : IBaseService<PostRequestTo, PostResponseTo>
{
    private readonly PostModerationProducer _kafkaProducer;
    private readonly IModerationResultWaiter _resultWaiter;
    private readonly IDistributedCache _cache;

    private const string AllPostsCacheKey = "Post:all";

    public PostService(PostModerationProducer kafkaProducer, IModerationResultWaiter resultWaiter, IDistributedCache cache)
    {
        _kafkaProducer = kafkaProducer;
        _resultWaiter = resultWaiter;
        _cache = cache;
    }

    // --- МЕТОД GET ALL ---
    public async Task<List<PostResponseTo>> GetAllAsync()
    {
        // 1. Проверяем кэш
        var cachedData = await _cache.GetStringAsync(AllPostsCacheKey);
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<List<PostResponseTo>>(cachedData) ?? new List<PostResponseTo>();
        }

        // 2. Если в кэше пусто — запрашиваем через Kafka
        const string queryKey = "all_query";
        await _kafkaProducer.SendAsync(queryKey, MessageType.GetAll, null);

        // Ожидаем результат из OutTopic (например, 1 секунду)
        var results = await _resultWaiter.WaitForListResultAsync(queryKey, TimeSpan.FromSeconds(1));
        var response = results ?? new List<PostResponseTo>();

        // 3. Сохраняем в кэш, если получили данные
        if (response.Any())
        {
            await _cache.SetStringAsync(AllPostsCacheKey, JsonSerializer.Serialize(response),
                new DistributedCacheEntryOptions { AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(10) });
        }

        return response;
    }

    public async Task<PostResponseTo> CreateAsync(PostRequestTo entity)
    {
        entity.Id = Math.Abs(Guid.NewGuid().GetHashCode());
        entity.State = PostState.PENDING;

        await _kafkaProducer.SendAsync(entity.Id.ToString(), MessageType.Create, entity);

        // Маппинг ответа (убедитесь, что поля заполнены)
        var response = new PostResponseTo
        {
            Id = entity.Id,
            Content = entity.Content,
            State = entity.State,
            StoryId = entity.StoryId
        };

        await _cache.SetStringAsync($"Post:{entity.Id}", JsonSerializer.Serialize(response));

        // Инвалидация списка всех постов
        await _cache.RemoveAsync(AllPostsCacheKey);

        return response;
    }

    public async Task<PostResponseTo?> UpdateAsync(PostRequestTo entity)
    {
        await _kafkaProducer.SendAsync(entity.Id.ToString(), MessageType.Update, entity);
        var result = await _resultWaiter.WaitForResultAsync(entity.Id, TimeSpan.FromSeconds(1));

        if (result != null)
        {
            await _cache.RemoveAsync($"Post:{entity.Id}");
            // Инвалидация списка всех постов
            await _cache.RemoveAsync(AllPostsCacheKey);
        }

        return result;
    }

    public async Task<bool> DeleteByIdAsync(int id)
    {
        await _kafkaProducer.SendAsync(id.ToString(), MessageType.Delete, id);
        var result = await _resultWaiter.WaitForResultAsync(id, TimeSpan.FromSeconds(1));

        if (result != null)
        {
            await _cache.RemoveAsync($"Post:{id}");
            // Инвалидация списка всех постов
            await _cache.RemoveAsync(AllPostsCacheKey);
        }

        return result != null;
    }

    public async Task<PostResponseTo?> GetByIdAsync(int id)
    {
        string cacheKey = $"Post:{id}";
        var cached = await _cache.GetStringAsync(cacheKey);
        if (cached != null) return JsonSerializer.Deserialize<PostResponseTo>(cached);

        await _kafkaProducer.SendAsync(id.ToString(), MessageType.Get, id);
        var result = await _resultWaiter.WaitForResultAsync(id, TimeSpan.FromSeconds(1));

        if (result != null)
        {
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(result),
                new DistributedCacheEntryOptions { AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(10) });
        }
        return result;
    }
}