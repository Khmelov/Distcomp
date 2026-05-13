using Publisher.Application.Repositories;
using Publisher.Domain.Models;
using Publisher.Infrastructure.DbContexts;

namespace Publisher.Infrastructure.Repositories;

public class TopicRepository : GenericRepository<Topic>, ITopicRepository
{
    public TopicRepository(PublisherDbContext context) : base(context)
    {
    }
}
