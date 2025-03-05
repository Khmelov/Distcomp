using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class InMemoryRepository<T> : IRepository<T> where T : class
    {
        private static readonly Dictionary<long, T> _storage = new();
        private long _currentId = 1;

        public Task<T> GetByIdAsync(long id) => Task.FromResult(_storage.GetValueOrDefault(id));

        public Task<IEnumerable<T>> GetAllAsync() => Task.FromResult<IEnumerable<T>>(_storage.Values);

        public Task<T> CreateAsync(T entity)
        {
            var idProperty = typeof(T).GetProperty("Id");
            if (idProperty != null && idProperty.PropertyType == typeof(long))
            {
                idProperty.SetValue(entity, _currentId++);
                _storage[_currentId - 1] = entity;
            }
            return Task.FromResult(entity);
        }

        public Task<T> UpdateAsync(T entity)
        {
            var idProperty = typeof(T).GetProperty("Id");
            if (idProperty != null && idProperty.PropertyType == typeof(long))
            {
                var id = (long)idProperty.GetValue(entity);
                if (_storage.ContainsKey(id))
                {
                    _storage[id] = entity;
                }
            }
            return Task.FromResult(entity);
        }

        public Task<bool> DeleteAsync(long id) => Task.FromResult(_storage.Remove(id));
    }

}
