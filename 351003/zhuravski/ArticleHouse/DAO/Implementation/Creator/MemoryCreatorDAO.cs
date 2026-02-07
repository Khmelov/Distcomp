using System.Collections.Concurrent;
using ArticleHouse.DAO.Exceptions;

namespace ArticleHouse.DAO.CreatorDAO;

public class MemoryCreatorDAO : ICreatorDAO
{
    private readonly ConcurrentDictionary<long, CreatorModel> models = [];
    private long nextModel = 0;

    public async Task<CreatorModel[]> GetAllCreatorsAsync()
    {
        return [.. models.Values];
    }

    public async Task<CreatorModel> AddNewCreatorAsync(CreatorModel model)
    {
        long id = Interlocked.Increment(ref nextModel);
        CreatorModel result = model.Clone();
        result.Id = id;
        models.GetOrAdd(id, result);
        return result;
    }

    public async Task DeleteCreatorAsync(long creatorId)
    {
        if (!models.TryRemove(creatorId, out CreatorModel? model))
        {
            throw new DAOObjectNotFoundException($"There is not a creator with id={creatorId}.");
        }
    }

    public async Task<CreatorModel> GetCreatorByIdAsync(long creatorId)
    {
        CreatorModel? result;
        if (!models.TryGetValue(creatorId, out result))
        {
            throw new DAOObjectNotFoundException($"There is not a creator with id={creatorId}.");
        }
        return result!;
    }

    public async Task<CreatorModel> UpdateCreatorAsync(CreatorModel model)
    {
        CreatorModel result = model.Clone();
        models.AddOrUpdate(
            model.Id,
            key => throw new DAOObjectNotFoundException($"There is not a creator with id={model.Id}."),
            (key, oldVal) => result
        );
        return result;
    }
}