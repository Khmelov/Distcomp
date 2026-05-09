using System.Text.Json;
using AutoMapper;
using BusinessLogic.Repositories;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Microsoft.Extensions.Caching.Distributed;

public class BaseService<TEntity, TEntityRequest, TEntityResponse> : IBaseService<TEntityRequest, TEntityResponse>
    where TEntity : BaseEntity
    where TEntityRequest : class
    where TEntityResponse : BaseEntity
{
    protected readonly IRepository<TEntity> _repository;
    protected readonly IMapper _mapper;
    protected readonly IDistributedCache _cache;

    public BaseService(IRepository<TEntity> repository, IMapper mapper, IDistributedCache cache)
    {
        _repository = repository;
        _mapper = mapper;
        _cache = cache;
    }

    // Ключ кэша на основе имени типа и ID
    protected string GetCacheKey(int id) => $"{typeof(TEntity).Name}:{id}";

    public async virtual Task<List<TEntityResponse>> GetAllAsync()
    {
        var cacheKey = $"{typeof(TEntity).Name}:all";

        // 1. Пытаемся взять из кэша
        var cachedData = await _cache.GetStringAsync(cacheKey);
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<List<TEntityResponse>>(cachedData) ?? new List<TEntityResponse>();
        }

        // 2. Если нет — идем в БД
        var entities = await _repository.GetAllAsync();
        var response = _mapper.Map<List<TEntityResponse>>(entities);

        // 3. Сохраняем в кэш
        var options = new DistributedCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(10)
        };
        await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(response), options);

        return response;
    }

    public async virtual Task<TEntityResponse?> GetByIdAsync(int id)
    {
        var cacheKey = GetCacheKey(id);

        // 1. Сначала идем в кэш
        var cachedData = await _cache.GetStringAsync(cacheKey);
        if (!string.IsNullOrEmpty(cachedData))
        {
            return JsonSerializer.Deserialize<TEntityResponse>(cachedData);
        }

        // 2. Если нет в кэше — идем в БД
        var entity = await _repository.GetByIdAsync(id);
        if (entity == null) return null;

        var response = _mapper.Map<TEntityResponse>(entity);

        // 3. Сохраняем обратно в кэш
        await SetCacheAsync(cacheKey, response);

        return response;
    }

    // Вспомогательный метод для очистки общего списка
    protected async Task InvalidateAllCacheAsync()
    {
        await _cache.RemoveAsync($"{typeof(TEntity).Name}:all");
    }

    public async virtual Task<TEntityResponse> CreateAsync(TEntityRequest entityRequest)
    {
        TEntity entity = _mapper.Map<TEntity>(entityRequest);
        entity.Id = await _repository.GetLastIdAsync() + 1;
        await _repository.CreateAsync(entity);

        var response = _mapper.Map<TEntityResponse>(entity);

        // ВАЖНО: Очищаем список "всех", так как он изменился
        await InvalidateAllCacheAsync();

        return response;
    }

    public async virtual Task<TEntityResponse?> UpdateAsync(TEntityRequest entityRequest)
    {
        var entity = _mapper.Map<TEntity>(entityRequest);
        if (await _repository.ExistsAsync(entity.Id))
        {
            await _repository.UpdateAsync(entity);
            await _cache.RemoveAsync(GetCacheKey(entity.Id));

            // ВАЖНО: Очищаем список "всех"
            await InvalidateAllCacheAsync();

            return _mapper.Map<TEntityResponse>(entity);
        }
        return null;
    }

    public async virtual Task<bool> DeleteByIdAsync(int id)
    {
        if (await _repository.ExistsAsync(id))
        {
            await _repository.DeleteAsync(id);
            await _cache.RemoveAsync(GetCacheKey(id));

            // ВАЖНО: Очищаем список "всех"
            await InvalidateAllCacheAsync();

            return true;
        }
        return false;
    }

    protected async Task SetCacheAsync(string key, TEntityResponse value)
    {
        var options = new DistributedCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(10) // Время жизни кэша
        };
        await _cache.SetStringAsync(key, JsonSerializer.Serialize(value), options);
    }
}