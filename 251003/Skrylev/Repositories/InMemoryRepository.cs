public class InMemoryRepository<TEntity> : IGenericRepository<TEntity> where TEntity : BaseEntity
{
    private readonly List<TEntity> _entities = new List<TEntity>();
    private int _nextId = 1;

    public async Task<TEntity> GetByIdAsync(int id)
    {
        return await Task.FromResult(_entities.FirstOrDefault(e => e.id == id));
    }

    public async Task<IEnumerable<TEntity>> GetAllAsync()
    {
        return await Task.FromResult(_entities.AsEnumerable());
    }

    public async Task<TEntity> AddAsync(TEntity entity)
    {
        entity.id = _nextId++;
        _entities.Add(entity);
        return await Task.FromResult(entity);
    }

    public async Task<TEntity> UpdateAsync(TEntity entity)
    {
        var existingEntity = await GetByIdAsync(entity.id);
        if (existingEntity == null)
            return null;

        var index = _entities.IndexOf(existingEntity);
        if (index >= 0)
        {
            _entities[index] = entity;
        }
        return await Task.FromResult(entity);
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var entity = await GetByIdAsync(id);
        if (entity == null)
            return false;

        _entities.Remove(entity);
        return await Task.FromResult(true);
    }

    public Task<bool> ExistsByLoginAsync(string login)
    {
        throw new NotImplementedException();
    }

    public Task<bool> ExistsByIdAsync(int id)
    {
        throw new NotImplementedException();
    }

    public Task<bool> ExistsByTitleAsync(string title)
    {
        throw new NotImplementedException();
    }
}
