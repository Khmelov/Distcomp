using Domain.Entities;

namespace Domain.Repositories;

public interface IUserRepository : IRepositoryBase<User>
{
    Task<User?> FindUserByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default);
}