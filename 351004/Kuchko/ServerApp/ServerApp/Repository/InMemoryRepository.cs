using System.Collections.Concurrent;
using ServerApp.Models.Entities;

namespace ServerApp.Repository;

public class InMemoryRepository<T> : IRepository<T> where T : BaseEntity
{
    protected readonly ConcurrentDictionary<long, T> _storage = new();
    private long _currentId;

    public IEnumerable<T> GetAll()
    {
        return _storage.Values;
    }

    public T? GetById(long id)
    {
        return _storage.TryGetValue(id, out var entity) ? entity : null;
    }

    public T Create(T entity)
    {
        entity.Id = Interlocked.Increment(ref _currentId);
        _storage[entity.Id] = entity;
        return entity;
    }

    public T Update(T entity)
    {
        if (!_storage.ContainsKey(entity.Id)) throw new KeyNotFoundException();
        _storage[entity.Id] = entity;
        return entity;
    }

    public bool Delete(long id)
    {
        return _storage.TryRemove(id, out _);
    }
}