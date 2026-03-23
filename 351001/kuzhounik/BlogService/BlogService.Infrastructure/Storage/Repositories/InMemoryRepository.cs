using BlogService.Domain.Interfaces;

namespace BlogService.Infrastructure.Storage.Repositories;

public class InMemoryRepository<ID, Entity> : IRepository<ID, Entity>
    where Entity : class, IEntity<ID>
{
    private readonly List<Entity> _storage = new();
    private long _nextId = 1;

    public Task AddAsync(Entity entity)
    {
        if (EqualityComparer<ID>.Default.Equals(entity.ID, default))
        {
            entity.ID = (ID)(object)_nextId++;
        }

        _storage.Add(entity);
        return Task.CompletedTask;
    }

    public Task<Entity?> GetByIdAsync(ID id)
    {
        var entity = _storage.FirstOrDefault(e => EqualityComparer<ID>.Default.Equals(e.ID, id));
        return Task.FromResult(entity);
    }

    public Task<IEnumerable<Entity>> GetAllAsync()
        => Task.FromResult<IEnumerable<Entity>>(_storage);

    public Task UpdateAsync(Entity entity)
    {
        var index = _storage.FindIndex(e => EqualityComparer<ID>.Default.Equals(e.ID, entity.ID));
        if (index != -1)
            _storage[index] = entity;

        return Task.CompletedTask;
    }

    public Task DeleteAsync(ID id)
    {
        var entity = _storage.FirstOrDefault(e => EqualityComparer<ID>.Default.Equals(e.ID, id));
        if (entity != null)
            _storage.Remove(entity);

        return Task.CompletedTask;
    }
}