using Publisher.Domain.Models;

namespace Publisher.Application.Repositories;

public interface IUserRepository : IGenericRepository<User>
{
    Task<User?> GetUserByLogin(string login);
}
