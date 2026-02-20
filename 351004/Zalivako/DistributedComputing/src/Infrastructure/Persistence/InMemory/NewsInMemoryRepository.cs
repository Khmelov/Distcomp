using Application.Interfaces;
using Core.Entities;

namespace Infrastructure.Persistence.InMemory
{
    public class NewsInMemoryRepository : InMemoryRepository<News>, INewsRepository
    {
    }
}
