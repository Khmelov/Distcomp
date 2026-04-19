using BusinessLogic.Repositories;
using DataAccess.Models;

namespace Infrastructure.RepositoriesImplementation
{
    public class InMemoryRepository<TEntity> : IRepository<TEntity> where TEntity : BaseEntity
    {
        protected readonly Dictionary<int, TEntity> _entities = new();

        public List<TEntity> GetAll()
        {
            return _entities.Values.ToList();
        }
        public TEntity GetById(int id)
        {
            return _entities[id];
        }
        public TEntity Create(TEntity entity)
        {
            _entities[entity.Id] = entity;
            return entity;
        }
        public TEntity Update(TEntity entity)
        {
            _entities[entity.Id] = entity;
            return entity;
        }
        public void Delete(int id)
        {
            _entities.Remove(id);
        }
        public bool Exists(int id)
        {
            if (_entities.ContainsKey(id))
                return true;
            return false;
        }
        public int GetLastId()
        {
            return _entities.Keys.DefaultIfEmpty(0).Max();
        }
    }
}
