public class CommentRepository : IRepository<Comment>
{
    private readonly Dictionary<long, Comment> _storage = new();
    private long _nextId = 1;

    public Comment? GetById(long id)
    {
        _storage.TryGetValue(id, out var comment);
        return comment;
    }

    public IEnumerable<Comment> GetAll() => _storage.Values;

    public Comment Create(Comment entity)
    {
        entity.Id = _nextId++;
        _storage[entity.Id] = entity;
        return entity;
    }

    public Comment Update(Comment entity)
    {
        if (!_storage.ContainsKey(entity.Id))
            throw new KeyNotFoundException($"Comment с id={entity.Id} не найден");
        
        _storage[entity.Id] = entity;
        return entity;
    }

    public bool Delete(long id) => _storage.Remove(id);
}