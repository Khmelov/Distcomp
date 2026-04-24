using Publisher.Domain.Entities;
using Publisher.Infrastructure.Abstractions;

namespace Publisher.Infrastructure.Repositories;

public class DbAuthorRepository : DbBaseRepository<Author>
{
    public DbAuthorRepository(AppDbContext context) : base(context) { }
}