public class LabelRepository : IRepository<Label>
{
    private readonly Dictionary<long, Label> _storage = new();
    private long _nextId = 1;

    public Label? GetById(long id)
    {
        _storage.TryGetValue(id, out var label);
        return label;
    }

    public IEnumerable<Label> GetAll() => _storage.Values;

    public Label Create(Label entity)
    {
        entity.Id = _nextId++;
        _storage[entity.Id] = entity;
        return entity;
    }

    public Label Update(Label entity)
    {
        if (!_storage.ContainsKey(entity.Id))
            throw new KeyNotFoundException($"Label с id={entity.Id} не найден");
        
        _storage[entity.Id] = entity;
        return entity;
    }

    public bool Delete(long id) => _storage.Remove(id);
}