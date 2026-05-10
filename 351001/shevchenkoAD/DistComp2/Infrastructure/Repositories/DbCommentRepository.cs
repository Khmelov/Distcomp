using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class DbCommentRepository : DbBaseRepository<Comment>
{
    public DbCommentRepository(AppDbContext context) : base(context) { }
}