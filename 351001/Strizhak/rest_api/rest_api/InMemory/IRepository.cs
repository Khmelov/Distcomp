using rest_api.Entities;

namespace rest_api.InMemory
{
    public interface IRepository<T> where T: IEntity
    {
        T GetById(long id);
        void Delete(long id);
        void Update(T entity);
        void Add(T entity);
        IEnumerable<T> GetAll();
        IEnumerable<T> Find(Func<T, bool> predicate);
    }
}
