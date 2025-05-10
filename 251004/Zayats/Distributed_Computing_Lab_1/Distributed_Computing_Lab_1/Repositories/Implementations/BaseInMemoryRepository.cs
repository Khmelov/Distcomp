using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;

namespace Distributed_Computing_Lab_1.Repositories.Implementations;

public abstract class BaseInMemoryRepository<TEntity> : IRepository<TEntity>
    where TEntity : BaseModel
{
    private readonly Dictionary<long, TEntity> _entities = [];
    private long _idCounter;
    
    public virtual async Task<IEnumerable<TEntity>> GetAllAsync()
    {
        return await Task.FromResult(_entities.Values.ToList());
    }

    public virtual async Task<TEntity?> GetByIdAsync(long id)
    {
        _entities.TryGetValue(id, out var entity);
        return await Task.FromResult(entity);
    }

    public virtual async Task<TEntity> CreateAsync(TEntity entity)
    {
        await Task.Run(() =>
        {
            var id = Interlocked.Increment(ref _idCounter);
            entity.Id = id;
            _entities.TryAdd(id, entity);
        });

        return entity;
    }

    public virtual async Task<TEntity?> UpdateAsync(TEntity entity)
    {
        await Task.Run(() =>
        {
            if (_entities.ContainsKey(entity.Id))
            {
                _entities[entity.Id] = entity;
            }
            else
            {
                entity = null!;
            }
        });
        return entity;
    }

    public virtual async Task<TEntity?> DeleteAsync(long id)
    {
        TEntity? result = null;
        await Task.Run(() =>
        {
            _entities.Remove(id, out result);
        });
        return result;
    }
}