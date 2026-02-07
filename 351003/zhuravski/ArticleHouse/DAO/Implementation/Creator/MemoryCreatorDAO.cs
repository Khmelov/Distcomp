using System.Collections.Concurrent;

namespace ArticleHouse.DAO.CreatorDAO;

public class MemoryCreatorDAO : ICreatorDAO
{
    private readonly ConcurrentDictionary<long, CreatorModel> models = [];
    private long nextModel = 0;

    public async Task<CreatorModel[]> GetAllCreators()
    {
        return [.. models.Values];
    }

    public async Task<CreatorModel> AddNewCreator(CreatorModel model)
    {
        long id = Interlocked.Increment(ref nextModel);
        CreatorModel result = model.Clone();
        result.Id = id;
        models.GetOrAdd(id, result);
        return result;
    }
}