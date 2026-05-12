using AutoMapper;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Dto;
using Publisher.Model;
using Publisher.Repository;
using System.Text.Json;

namespace Publisher.Service {
    public abstract class BaseService<TEntity, TRequest, TResponse>
    where TEntity : BaseEntity
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo {
        protected readonly IRepository<TEntity> _repository;
        protected readonly IMapper _mapper;
        protected readonly ILogger _logger;
        protected readonly IDistributedCache _cache;
        protected string _cacheKeyPrefix = "editor:";

        protected BaseService(IRepository<TEntity> repository, IMapper mapper, ILogger logger, IDistributedCache cache) {
            _repository = repository;
            _mapper = mapper;
            _logger = logger;
            _cache = cache;
        }

        public async Task SetRecordAsync<T>(string recordId, T data, TimeSpan? absoluteExpireTime = null, TimeSpan? unusedExpireTime = null) {
            var options = new DistributedCacheEntryOptions();

            options.AbsoluteExpirationRelativeToNow = absoluteExpireTime ?? TimeSpan.FromSeconds(60);
            options.SlidingExpiration = unusedExpireTime;

            var jsonData = JsonSerializer.Serialize(data);
            await _cache.SetStringAsync(recordId, jsonData, options);
        }

        public virtual async Task<IEnumerable<TResponse>> GetAllAsync() {
            var entities = await _repository.GetAllAsync();
            return _mapper.Map<IEnumerable<TResponse>>(entities);
        }

        public virtual async Task<TResponse?> GetByIdAsync(long id) {
            string key = $"{_cacheKeyPrefix}{id}";

            var cachedData = await _cache.GetStringAsync(key);
            if (!string.IsNullOrEmpty(cachedData)) {
                return JsonSerializer.Deserialize<TResponse>(cachedData);
            }
            var entity = await _repository.GetByIdAsync(id);
            if (entity != null) await SetRecordAsync($"editor:{id}", entity, TimeSpan.FromMinutes(5));
            return entity == null ? null : _mapper.Map<TResponse>(entity);
        }

        public virtual async Task<TResponse> AddAsync(TRequest request) {
            var entity = _mapper.Map<TEntity>(request);
            var createdEntity = await _repository.AddAsync(entity);
            return _mapper.Map<TResponse>(createdEntity);
        }

        public virtual async Task<TResponse?> UpdateAsync(TRequest request) {
            try {
                _logger.LogInformation($"Updating {typeof(TEntity).Name} with ID: {request.Id}");

                if (request.Id <= 0) {
                    _logger.LogWarning($"Invalid ID: {request.Id}");
                    return null;
                }

                var exists = await _repository.ExistsAsync(request.Id);
                if (!exists) {
                    _logger.LogWarning($"{typeof(TEntity).Name} with ID {request.Id} not found");
                    return null;
                }

                var existingEntity = await _repository.GetByIdAsync(request.Id);
                if (existingEntity == null) {
                    return null;
                }

                _mapper.Map(request, existingEntity);

                var updatedEntity = await _repository.UpdateAsync(existingEntity);
                if (updatedEntity != null) {
                    await _cache.RemoveAsync($"{_cacheKeyPrefix}{updatedEntity.Id}");
                }
                var response = _mapper.Map<TResponse>(updatedEntity);

                _logger.LogInformation($"Successfully updated {typeof(TEntity).Name} with ID: {request.Id}");
                return response;
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error updating {typeof(TEntity).Name} with ID: {request.Id}");
                throw;
            }
        }

        public virtual async Task<bool> DeleteAsync(long id) {
            var deleted = await _repository.DeleteAsync(id);
            if (deleted) {
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{id}");
            }
            return deleted;
        }
    }
}