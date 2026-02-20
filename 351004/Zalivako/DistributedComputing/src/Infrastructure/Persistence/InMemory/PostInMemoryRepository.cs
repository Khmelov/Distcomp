using Application.Interfaces;
using Core.Entities;

namespace Infrastructure.Persistence.InMemory
{
    public class PostInMemoryRepository : InMemoryRepository<Post>, IPostRepository
    {

    }
}
