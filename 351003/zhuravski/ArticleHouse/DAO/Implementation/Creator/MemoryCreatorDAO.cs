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
}