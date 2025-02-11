using Domain.Entities;
using Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public class UserRepository : RepositoryBase<User>, IUserRepository
{
    public UserRepository(RepositoryContext context) : base(context)
    {
    }

    public async Task<User?> FindUserByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default)
    {
        var user = await FindByCondition(user => user.Id == id, trackChanges).SingleOrDefaultAsync(cancellationToken);
        
        return user;
    }
}