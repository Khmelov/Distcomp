using Publisher.Domain.Entities;
using Publisher.Infrastructure.Abstractions;

namespace Publisher.Infrastructure.Repositories;

public class DbLabelRepository : DbBaseRepository<Label>
{
    public DbLabelRepository(AppDbContext context) : base(context)
    {
    }
}