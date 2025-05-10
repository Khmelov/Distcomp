using Publisher.Data;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class DatabaseAuthorRepository : BaseDatabaseRepository<Author>, IAuthorRepository
{
    public DatabaseAuthorRepository(AppDbContext context) : base(context)
    {
        
    }
}