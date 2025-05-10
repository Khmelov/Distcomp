using Publisher.Data;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class DatabaseTopicRepository : BaseDatabaseRepository<Topic>, ITopicRepository
{
    public DatabaseTopicRepository(AppDbContext context) : base(context)
    {
    }
}