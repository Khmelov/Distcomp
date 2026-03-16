using Project.Model;

namespace Project.Repository {
    public class InMemoryRepository<T> : IRepository<T> where T : BaseEntity {
        private readonly Dictionary<long, T> _data = new();
        private long _nextId = 1;
        private readonly object _lock = new();

        public Task<T?> GetByIdAsync(long id) {
            lock (_lock) {
                _data.TryGetValue(id, out var entity);
                return Task.FromResult(entity);
            }
        }

        public Task<IEnumerable<T>> GetAllAsync() {
            lock (_lock) {
                return Task.FromResult(_data.Values.AsEnumerable());
            }
        }

        public Task<T> AddAsync(T entity) {
            lock (_lock) {
                entity.Id = _nextId++;
                _data[entity.Id] = entity;
                return Task.FromResult(entity);
            }
        }

        public Task<T> UpdateAsync(T entity) {
            lock (_lock) {
                if (!_data.ContainsKey(entity.Id))
                    throw new KeyNotFoundException($"Entity with id {entity.Id} not found");

                _data[entity.Id] = entity;
                return Task.FromResult(entity);
            }
        }

        public Task<bool> DeleteAsync(long id) {
            lock (_lock) {
                return Task.FromResult(_data.Remove(id));
            }
        }

        public Task<bool> ExistsAsync(long id) {
            lock (_lock) {
                return Task.FromResult(_data.ContainsKey(id));
            }
        }
    }
}