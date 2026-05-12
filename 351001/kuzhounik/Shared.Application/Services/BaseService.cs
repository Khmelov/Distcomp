using System.Text.Json;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.DTOs;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Interfaces.Services;
using Shared.Domain.Interfaces;

namespace Shared.Application.Services;

public abstract class BaseService<ID, Entity, RequestDto, ResponseDto>
    : IService<ID, RequestDto, ResponseDto>
    where Entity : class, IEntity<ID>
    where RequestDto : class, IRequestDto<ID>
    where ResponseDto : class, IResponseDto<ID>
{
    protected readonly IRepository<ID, Entity> _repository;
    protected readonly IRequestMapper<RequestDto, Entity> _requestMapper;
    protected readonly IResponseMapper<Entity, ResponseDto> _responseMapper;
    protected readonly IDistributedCache? _cache;
    
    protected virtual DistributedCacheEntryOptions CacheOptions => new()
    {
        AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(10),
        SlidingExpiration = TimeSpan.FromMinutes(2)
    };
    
    protected BaseService(
        IRepository<ID, Entity> repository,
        IRequestMapper<RequestDto, Entity> requestMapper,
        IResponseMapper<Entity, ResponseDto> responseMapper,
        IDistributedCache? cache) // Сделал nullable
    {
        _repository = repository;
        _requestMapper = requestMapper;
        _responseMapper = responseMapper;
        _cache = cache;
    }

    protected virtual Task OnBeforeCreateAsync(RequestDto request) => Task.CompletedTask;
    
    protected virtual string GetCacheKey(ID id) 
    {
        var type = typeof(Entity);
        string name = type.IsGenericType 
            ? type.Name.Substring(0, type.Name.IndexOf('`')) 
            : type.Name;
        return $"{name}:{id}";
    }
    
    public virtual async Task<ResponseDto> CreateAsync(RequestDto request)
    {
        var entity = _requestMapper.Map(request); 
        await _repository.AddAsync(entity); 
        
        // Инвалидируем кэш, чтобы гарантировать чистоту
        if (_cache != null)
        {
            await _cache.RemoveAsync(GetCacheKey(entity.ID));
        }

        return _responseMapper.Map(entity);
    }

    public virtual async Task<ResponseDto?> GetAsync(ID id)
    {
        string key = GetCacheKey(id);

        // 1. Проверяем Redis
        if (_cache != null)
        {
            var cachedJson = await _cache.GetStringAsync(key);
            if (!string.IsNullOrEmpty(cachedJson))
            {
                return JsonSerializer.Deserialize<ResponseDto>(cachedJson);
            }
        }

        // 2. Идем в СВОЮ базу (Postgres для Блога, Cassandra для Дискуссий)
        var entity = await _repository.GetByIdAsync(id);
        if (entity == null) return null;

        var response = _responseMapper.Map(entity);

        // 3. Кладем в кэш
        if (_cache != null)
        {
            await _cache.SetStringAsync(key, JsonSerializer.Serialize(response), CacheOptions);
        }

        return response;
    }

    public virtual async Task<ResponseDto?> UpdateAsync(RequestDto request)
    {
        var existing = await _repository.GetByIdAsync(request.ID);
        if (existing is null) return null;

        var updatedEntity = _requestMapper.Map(request);
        updatedEntity.ID = request.ID;

        // Сначала удаляем кэш (превентивно)
        if (_cache != null) await _cache.RemoveAsync(GetCacheKey(request.ID));

        await _repository.UpdateAsync(updatedEntity);

        // Снова удаляем кэш (после записи в базу)
        if (_cache != null) await _cache.RemoveAsync(GetCacheKey(request.ID));

        return _responseMapper.Map(updatedEntity);
    }

    public virtual async Task DeleteAsync(ID id)
    {
        await _repository.DeleteAsync(id);
        if (_cache != null)
        {
            await _cache.RemoveAsync(GetCacheKey(id));
        }
    }

    public virtual async Task<IEnumerable<ResponseDto>> GetAllAsync()
    {
        var entities = await _repository.GetAllAsync();
        return entities.Select(entity => _responseMapper.Map(entity));
    }
}