using AutoMapper;
using Publisher.Dto;
using Publisher.Model;
using Publisher.Repository;

namespace Publisher.Service {
    public abstract class BaseService<TEntity, TRequest, TResponse>
    where TEntity : BaseEntity
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo {
        protected readonly IRepository<TEntity> _repository;
        protected readonly IMapper _mapper;
        protected readonly ILogger _logger;

        protected BaseService(IRepository<TEntity> repository, IMapper mapper, ILogger logger) {
            _repository = repository;
            _mapper = mapper;
            _logger = logger;
        }

        public virtual async Task<IEnumerable<TResponse>> GetAllAsync() {
            var entities = await _repository.GetAllAsync();
            return _mapper.Map<IEnumerable<TResponse>>(entities);
        }

        public virtual async Task<TResponse?> GetByIdAsync(long id) {
            var entity = await _repository.GetByIdAsync(id);
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
            return await _repository.DeleteAsync(id);
        }
    }
}