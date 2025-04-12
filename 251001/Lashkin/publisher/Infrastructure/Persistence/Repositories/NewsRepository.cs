using Domain.Entities;
using Domain.Interfaces;

namespace Persistence.Repositories;

public class NewsRepository : RepositoryBase<News>, INewsRepository
{
    public NewsRepository(RepositoryContext context) : base(context)
    {
    }
}