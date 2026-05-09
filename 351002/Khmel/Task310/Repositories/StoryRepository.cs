public class StoryRepository : IRepository<Story>
{
    private readonly Dictionary<long, Story> _storage = new();
    private long _nextId = 1;

    public Story? GetById(long id)
    {
        _storage.TryGetValue(id, out var story);
        return story;
    }

    public IEnumerable<Story> GetAll() => _storage.Values;

    public Story Create(Story entity)
    {
        entity.Id = _nextId++;
        entity.Created = DateTime.UtcNow;  
        entity.Modified = DateTime.UtcNow;  
        _storage[entity.Id] = entity;
        return entity;
    }

    public Story Update(Story entity)
    {
        if (!_storage.ContainsKey(entity.Id))
            throw new KeyNotFoundException($"Story с id={entity.Id} не найдена");
        
        entity.Modified = DateTime.UtcNow; 
        _storage[entity.Id] = entity;
        return entity;
    }

    public bool Delete(long id) => _storage.Remove(id);
}