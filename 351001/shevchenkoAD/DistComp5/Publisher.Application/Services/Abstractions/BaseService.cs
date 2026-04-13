using AutoMapper;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Abstractions;
using Publisher.Domain.Interfaces;
using Shared.DTOs.Abstractions;
using Shared.Interfaces;

namespace Publisher.Application.Services.Abstractions;

public abstract class BaseService<TEntity, TRequest, TResponse> : IService<TRequest, TResponse>
    where TEntity : BaseEntity
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo
{
    protected readonly ICacheService _cache;
    protected readonly IMapper _mapper;
    protected readonly IRepository<TEntity> _repository;

    protected BaseService(IRepository<TEntity> repository,
        IMapper mapper,
        ICacheService cache)
    {
        _repository = repository;
        _mapper = mapper;
        _cache = cache;
    }

    protected abstract int NotFoundSubCode { get; }
    protected abstract string EntityName { get; }

    public virtual async Task<IEnumerable<TResponse>> GetAllAsync()
    {
        var entities = await _repository.GetAllAsync();
        return _mapper.Map<IEnumerable<TResponse>>(entities);
    }

    public virtual async Task<TResponse> GetByIdAsync(long id)
    {
        var cached = await _cache.GetAsync<TResponse>(GetCacheKey(id));
        if (cached != null) return cached;

        var entity = await _repository.GetByIdAsync(id);
        if (entity == null)
            ThrowNotFound(id);

        var response = _mapper.Map<TResponse>(entity);

        await _cache.SetAsync(GetCacheKey(id), response);

        return response;
    }

    public virtual async Task<TResponse> CreateAsync(TRequest request)
    {
        ValidateRequest(request);

        var entity = _mapper.Map<TEntity>(request);

        BeforeCreate(entity);

        var createdEntity = await _repository.CreateAsync(entity);

        var response = _mapper.Map<TResponse>(createdEntity);

        await _cache.SetAsync(GetCacheKey(response.Id ?? 0), response);

        return response;
    }

    public virtual async Task<TResponse> UpdateAsync(TRequest request)
    {
        var id = request.Id ?? -1;

        if (id < 0) throw new RestException(400, NotFoundSubCode, "Invalid ID in request body");

        ValidateRequest(request);

        var existingEntity = await _repository.GetByIdAsync(id);
        if (existingEntity == null)
            ThrowNotFound(id);

        _mapper.Map(request, existingEntity);

        BeforeUpdate(existingEntity);

        var updatedEntity = await _repository.UpdateAsync(existingEntity);

        var response = _mapper.Map<TResponse>(updatedEntity);

        await _cache.SetAsync(GetCacheKey(id), response);

        return response;
    }

    public virtual async Task<bool> DeleteAsync(long id)
    {
        var isDeleted = await _repository.DeleteAsync(id);
        if (!isDeleted)
            ThrowNotFound(id);

        await _cache.RemoveAsync(GetCacheKey(id));

        return true;
    }

    protected string GetCacheKey(long id)
    {
        return $"{EntityName}:{id}";
    }

    protected abstract void ValidateRequest(TRequest request);

    protected virtual void BeforeCreate(TEntity entity)
    {
    }

    protected virtual void BeforeUpdate(TEntity entity)
    {
    }

    protected void ThrowNotFound(long id)
    {
        throw new RestException(404, NotFoundSubCode, $"{EntityName} with id {id} not found");
    }
}