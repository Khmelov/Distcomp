using AutoMapper;
using rest_api.Entities;
using rest_api.Repositories;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace rest_api.Services
{
    /// <summary>
    /// Базовый класс для всех сервисов с асинхронными методами.
    /// </summary>
    public abstract class BaseService<T, TRequest, TResponse> : IService<T, TRequest, TResponse>
        where T : class, IEntity
    {
        protected readonly IRepository<T> _repository;
        protected readonly IMapper _mapper;

        protected BaseService(IRepository<T> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        /// <inheritdoc />
        public virtual async Task<TResponse?> GetByIdAsync(long id)
        {
            var entity = await _repository.GetByIdAsync(id);
            return entity == null ? default : _mapper.Map<TResponse>(entity);
        }

        /// <inheritdoc />
        public virtual async Task<IEnumerable<TResponse>> GetAllAsync()
        {
            var entities = await _repository.GetAllAsync();
            return _mapper.Map<IEnumerable<TResponse>>(entities);
        }

        /// <inheritdoc />
        public abstract Task<TResponse> CreateAsync(TRequest request);

        /// <inheritdoc />
        public abstract Task<TResponse> UpdateAsync(long id, TRequest request);

        /// <inheritdoc />
        public virtual async Task DeleteAsync(long id)
        {
            var entity = await _repository.GetByIdAsync(id);
            if (entity == null)
                throw new KeyNotFoundException($"Entity with id {id} not found");
            _repository.Delete(entity);
            await _repository.SaveChangesAsync();
        }
    }
}