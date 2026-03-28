using AutoMapper;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using DataAccess.Models;
using BusinessLogic.Repositories;

namespace BusinessLogic.Services
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

        public List<TEntityResponse> GetAll()
        {
            return _mapper.Map<List<TEntityResponse>>(_repository.GetAll());
        }
        public TEntityResponse? GetById(int id)
        {
            if (_repository.Exists(id))
            {
                return _mapper.Map<TEntityResponse>(_repository.GetById(id));
            }
            return null;
        }
        public bool DeleteById(int id)
        {
            if (_repository.Exists(id))
            {
                _repository.Delete(id);
                return true;
            }
            return false;
        }
        public virtual TEntityResponse Create(TEntityRequest entity)
        {
            TEntity creator = _mapper.Map<TEntity>(entity);
            creator.Id = _repository.GetLastId() + 1;
            _repository.Create(creator);
            return _mapper.Map<TEntityResponse>(creator);
        }
        public virtual TEntityResponse? Update(TEntityRequest entity)
        {
            var creator = _mapper.Map<TEntity>(entity);
            if (_repository.Exists(creator.Id))
            {
                _repository.Update(creator);
                return _mapper.Map<TEntityResponse>(creator);
            }
            return null;
        }

    }
}
