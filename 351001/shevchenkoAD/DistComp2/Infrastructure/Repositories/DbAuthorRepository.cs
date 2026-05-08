using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class DbAuthorRepository : DbBaseRepository<Author>
{
    public DbAuthorRepository(AppDbContext context) : base(context) { }
}