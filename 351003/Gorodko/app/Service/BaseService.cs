using AutoMapper;
using Project.Dto;
using Project.Model;
using Project.Repository;

namespace Project.Service {
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
            if (!await _repository.ExistsAsync(request.Id))
                return null;

            var entity = _mapper.Map<TEntity>(request);
            var updatedEntity = await _repository.UpdateAsync(entity);
            return _mapper.Map<TResponse>(updatedEntity);
        }

        public virtual async Task<bool> DeleteAsync(long id) {
            return await _repository.DeleteAsync(id);
        }
    }
}