using System.Collections.Concurrent;
using System.Runtime.CompilerServices;
using Domain.Abstractions;
using Domain.Interfaces;

namespace Infrastructure.Abstractions;

public abstract class LocalBaseRepository<T> : IRepository<T>
    where T : BaseEntity {
    protected readonly ConcurrentDictionary<long, T> _storage = new();
    private long _idCounter = 0;

    public virtual Task<IEnumerable<T>> GetAllAsync() {
        var result = _storage.Values.Select(item => Copy(item)).AsEnumerable();
        return Task.FromResult(result);
    }

    public virtual Task<T?> GetByIdAsync(long id) {
        _storage.TryGetValue(id, out var entity);
        return Task.FromResult(entity == null ? null : Copy(entity));
    }

    public virtual Task<T> CreateAsync(T entity) {
        long id = Interlocked.Increment(ref _idCounter);
        entity.Id = id;

        var entityToSave = Copy(entity);

        _storage.TryAdd(id, entityToSave);

        return Task.FromResult(Copy(entityToSave));
    }

    public virtual Task<T?> UpdateAsync(T entity) {
        if (!_storage.ContainsKey(entity.Id))
            return Task.FromResult<T?>(null);

        var entityToUpdate = Copy(entity);
        
        _storage[entity.Id] = entityToUpdate;

        return Task.FromResult<T?>(Copy(entityToUpdate));
    }

    public virtual Task<bool> DeleteAsync(long id) {
        bool removed = _storage.TryRemove(id, out _);
        return Task.FromResult(removed);
    }

    protected abstract T Copy(T src);
}

// public abstract class LocalBaseRepository<T> : IRepository<T>
//     where T : BaseEntity {
//     protected readonly object _lock = new();
//     protected readonly List<T> _storage = new();
//     private long _idCounter;
//
//     public virtual Task<IEnumerable<T>> GetAllAsync() {
//         lock (_lock) {
//             var result = _storage.Select(item => Copy(item)).AsEnumerable();
//             return Task.FromResult(result);
//         }
//     }
//
//     public virtual Task<T?> GetByIdAsync(long id) {
//         lock (_lock) {
//             var entity = _storage.FirstOrDefault(e => e.Id == id);
//             return Task.FromResult(entity == null ? null : Copy(entity));
//         }
//     }
//
//     public virtual Task<T> CreateAsync(T entity) {
//         lock (_lock) {
//             entity.Id = Interlocked.Increment(ref _idCounter);
//
//             var entityToSave = Copy(entity);
//             _storage.Add(entityToSave);
//
//             return Task.FromResult(Copy(entityToSave));
//         }
//     }
//
//     public virtual Task<T?> UpdateAsync(T entity) {
//         lock (_lock) {
//             var index = _storage.FindIndex(e => e.Id == entity.Id);
//             if (index == -1)
//                 return Task.FromResult<T?>(null);
//
//             var entityToUpdate = Copy(entity);
//             _storage[index] = entityToUpdate;
//
//             return Task.FromResult<T?>(Copy(entityToUpdate));
//         }
//     }
//
//     public virtual Task<bool> DeleteAsync(long id) {
//         lock (_lock) {
//             var entity = _storage.FirstOrDefault(e => e.Id == id);
//             if (entity == null)
//                 return Task.FromResult(false);
//
//             _storage.Remove(entity);
//             return Task.FromResult(true);
//         }
//     }
//
//     protected abstract T Copy(T src);
// }