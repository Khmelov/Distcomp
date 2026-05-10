using rest_api.Entities;
using rest_api.InMemory;

namespace rest_api.Services
{
    public abstract class BaseService<T, TRequest, TResponse> : IService<T, TRequest, TResponse>
        where T : class, IEntity 
    {
        protected readonly IRepository<T> _repository;

        protected BaseService(IRepository<T> repository)
        {
            _repository = repository;
        }

        public virtual TResponse? GetById(long id)
        {
            var entity = _repository.GetById(id);
            return entity == null ? default : MapToResponse(entity);
        }

        public virtual IEnumerable<TResponse> GetAll()
        {
            return _repository.GetAll().Select(MapToResponse);
        }

        public virtual TResponse Create(TRequest request)
        {
       
            throw new NotImplementedException();
        }

        public virtual TResponse Update(TRequest request)
        {
         
            throw new NotImplementedException();
        }

        public virtual void Delete(long id)
        {
            _repository.Delete(id);
        }

        protected abstract TResponse MapToResponse(T entity);
        protected abstract T MapToEntity(TRequest request);
    }
}