using RestApiTask.Models.Entities;
using System.Collections.Concurrent;

namespace RestApiTask.Repositories;

public class InMemoryRepository<T> : IRepository<T> where T : class, IHasId
{
    private readonly ConcurrentDictionary<long, T> _storage = new();
    private long _currentId = 0;

    public Task<IEnumerable<T>> GetAllAsync()
    {
        return Task.FromResult(_storage.Values.AsEnumerable());
    }

    public Task<T?> GetByIdAsync(long id)
    {
        _storage.TryGetValue(id, out var entity);
        return Task.FromResult(entity);
    }

    public Task<T> AddAsync(T entity)
    {
        // Атомарно увеличиваем ID
        long id = Interlocked.Increment(ref _currentId);
        entity.Id = id;
        _storage[id] = entity;
        return Task.FromResult(entity);
    }

    public Task<T> UpdateAsync(T entity)
    {
        if (_storage.ContainsKey(entity.Id))
        {
            _storage[entity.Id] = entity;
            return Task.FromResult(entity);
        }
        throw new KeyNotFoundException($"Entity with id {entity.Id} not found.");
    }

    public Task<bool> DeleteAsync(long id)
    {
        return Task.FromResult(_storage.TryRemove(id, out _));
    }
}