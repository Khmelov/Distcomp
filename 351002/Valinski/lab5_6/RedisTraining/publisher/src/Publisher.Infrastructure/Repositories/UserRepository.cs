using Microsoft.EntityFrameworkCore;
using Publisher.Application.Repositories;
using Publisher.Domain.Models;
using Publisher.Infrastructure.DbContexts;

namespace Publisher.Infrastructure.Repositories;

public class UserRepository : GenericRepository<User>, IUserRepository
{
    public UserRepository(PublisherDbContext context) : base(context)
    {
    }

    public async Task<User?> GetUserByLogin(string login)
    {
        return await DbSet.FirstOrDefaultAsync(u => u.Login == login);
    }
}
