using System.Collections.Concurrent;
using Distcomp.Application.Interfaces;

namespace Distcomp.Infrastructure.Repositories
{
    public class InMemoryRepository<T> : IRepository<T> where T : class
    {
        private readonly ConcurrentDictionary<long, T> _storage = new();
        private long _idCounter = 0;

        public T Create(T entity)
        {
            var idProperty = typeof(T).GetProperty("Id");
            if (idProperty == null) throw new Exception($"Entity {typeof(T).Name} must have a long Id property.");

            long newId = Interlocked.Increment(ref _idCounter);
            idProperty.SetValue(entity, newId);

            _storage[newId] = entity;
            return entity;
        }

        public T? GetById(long id) => _storage.TryGetValue(id, out var value) ? value : null;

        public IEnumerable<T> GetAll() => _storage.Values;

        public T Update(T entity)
        {
            var idProperty = typeof(T).GetProperty("Id");
            long id = (long)idProperty!.GetValue(entity)!;

            if (!_storage.ContainsKey(id)) throw new KeyNotFoundException("Entity not found");

            _storage[id] = entity;
            return entity;
        }

        public bool Delete(long id) => _storage.TryRemove(id, out _);

        public IEnumerable<T> GetPaged(int page, int pageSize, string sortBy)
        {
            var query = _storage.Values.AsQueryable();

            var prop = typeof(T).GetProperty(sortBy ?? "Id");
            if (prop != null)
            {
                query = query.OrderBy(x => prop.GetValue(x, null));
            }

            return query.Skip((page - 1) * pageSize).Take(pageSize).ToList();
        }
    }
}