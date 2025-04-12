using Domain.Entities;
using Domain.Interfaces;

namespace Persistence.Repositories;

public class UserRepository : RepositoryBase<User>, IUserRepository
{
    public UserRepository(RepositoryContext context) : base(context)
    {
    }
}