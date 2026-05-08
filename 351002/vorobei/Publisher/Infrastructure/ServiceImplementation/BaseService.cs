using AutoMapper;
using BusinessLogic.Servicies;
using DataAccess.Models;
using BusinessLogic.Repositories;

namespace Infrastructure.ServiceImplementation
{
    public class BaseService<TEntity, TEntityRequest, TEntityResponse> : IBaseService<TEntityRequest, TEntityResponse> 
                                                                                      where TEntity : BaseEntity
                                                                                      where TEntityRequest : class
                                                                                      where TEntityResponse : BaseEntity
    {
        protected readonly IRepository<TEntity> _repository;
        protected readonly IMapper _mapper;

        public BaseService(IRepository<TEntity> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        public async virtual Task<List<TEntityResponse>> GetAllAsync()
        {
            return _mapper.Map<List<TEntityResponse>>(await _repository.GetAllAsync());
        }
        public async virtual Task<TEntityResponse?> GetByIdAsync(int id)
        {
            if (await _repository.ExistsAsync(id))
            {
                return _mapper.Map<TEntityResponse>(await _repository.GetByIdAsync(id));
            }
            return null;
        }
        public async virtual Task<bool> DeleteByIdAsync(int id)
        {
            if (await _repository.ExistsAsync(id))
            {
                await _repository.DeleteAsync(id);
                return true;
            }
            return false;
        }
        public async virtual Task<TEntityResponse> CreateAsync(TEntityRequest entity)
        {
            TEntity creator = _mapper.Map<TEntity>(entity);
            creator.Id = await _repository.GetLastIdAsync() + 1;
            await _repository.CreateAsync(creator);
            return _mapper.Map<TEntityResponse>(creator);
        }
        public async virtual Task<TEntityResponse?> UpdateAsync(TEntityRequest entity)
        {
            var creator = _mapper.Map<TEntity>(entity);
            if (await _repository.ExistsAsync(creator.Id))
            {
                await _repository.UpdateAsync(creator);
                return _mapper.Map<TEntityResponse>(creator);
            }
            return null;
        }

    }
}
