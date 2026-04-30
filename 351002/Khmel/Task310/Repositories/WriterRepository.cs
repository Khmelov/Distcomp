public class WriterRepository : IRepository<Writer>
{
    private readonly Dictionary<long, Writer> _storage = new();
    private long _nextId = 1;

    public Writer? GetById(long id)
    {
        _storage.TryGetValue(id, out var writer);
        return writer;
    }

    public IEnumerable<Writer> GetAll()
    {
        return _storage.Values;
    }

    public Writer Create(Writer entity)
    {
        entity.Id = _nextId++; 
        _storage[entity.Id] = entity;
        return entity;
    }

    public Writer Update(Writer entity)
    {
        if (!_storage.ContainsKey(entity.Id))
            throw new KeyNotFoundException($"Writer с id={entity.Id} не найден");
        
        _storage[entity.Id] = entity;
        return entity;
    }

    public bool Delete(long id)
    {
        return _storage.Remove(id); 
    }
}