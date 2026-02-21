using System.Collections.Concurrent;
using ArticleHouse.DAO.Exceptions;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations.Memory;

public class MemoryDAO<T> : IDAO<T> where T : Model<T>
{
    private readonly ConcurrentDictionary<long, T> models = [];
    private long nextModel = 0;

    public async Task<T[]> GetAllAsync()
    {
        return [.. models.Values];
    }

    public async Task<T> AddNewAsync(T model)
    {
        long id = Interlocked.Increment(ref nextModel);
        T result = (T)model.Clone();
        result.Id = id;
        models.GetOrAdd(id, result);
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        if (!models.TryRemove(id, out T? model))
        {
            throw new DAOObjectNotFoundException($"There is not an object with id={id}.");
        }
    }

    public async Task<T> GetByIdAsync(long id)
    {
        T? result;
        if (!models.TryGetValue(id, out result))
        {
            throw new DAOObjectNotFoundException($"There is not an object with id={id}.");
        }
        return result!;
    }

    public async Task<T> UpdateAsync(T model)
    {
        T result = (T)model.Clone();
        models.AddOrUpdate(
            model.Id,
            key => throw new DAOObjectNotFoundException($"There is not an object with id={model.Id}."),
            (key, oldVal) => result
        );
        return result;
    }
}