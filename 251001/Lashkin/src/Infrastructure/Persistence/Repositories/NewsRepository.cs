using Domain.Entities;
using Domain.Repositories;

namespace Persistence.Repositories;

public class NewsRepository : RepositoryBase<News>, INewsRepository
{
    public NewsRepository(RepositoryContext context) : base(context)
    {
    }
}