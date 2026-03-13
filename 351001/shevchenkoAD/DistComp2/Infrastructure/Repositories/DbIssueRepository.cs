using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class DbIssueRepository : DbBaseRepository<Issue>
{
    public DbIssueRepository(AppDbContext context) : base(context) { }
}