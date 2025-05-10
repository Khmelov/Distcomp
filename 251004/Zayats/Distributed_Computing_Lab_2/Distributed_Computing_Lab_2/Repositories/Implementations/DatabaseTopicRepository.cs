using Distributed_Computing_Lab_2.Data;
using Distributed_Computing_Lab_2.Models;
using Distributed_Computing_Lab_2.Repositories.Interfaces;

namespace Distributed_Computing_Lab_2.Repositories.Implementations;

public class DatabaseTopicRepository : BaseDatabaseRepository<Topic>, ITopicRepository
{
    public DatabaseTopicRepository(AppDbContext context) : base(context)
    {
    }
}