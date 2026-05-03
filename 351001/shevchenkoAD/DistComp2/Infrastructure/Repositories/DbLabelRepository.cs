using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class DbLabelRepository : DbBaseRepository<Label>
{
    public DbLabelRepository(AppDbContext context) : base(context) { }
}