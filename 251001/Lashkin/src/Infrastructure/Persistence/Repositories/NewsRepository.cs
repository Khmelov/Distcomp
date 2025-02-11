using Domain.Entities;
using Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public class NewsRepository : RepositoryBase<News>, INewsRepository
{
    public NewsRepository(RepositoryContext context) : base(context)
    {
    }

    public async Task<News?> FindNewsByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default)
    {
        var news = await FindByCondition(news => news.Id == id, trackChanges).SingleOrDefaultAsync(cancellationToken);
        
        return news;
    }
}