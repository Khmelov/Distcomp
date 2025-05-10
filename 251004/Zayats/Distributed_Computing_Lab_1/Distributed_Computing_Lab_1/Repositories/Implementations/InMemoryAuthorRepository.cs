using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;

namespace Distributed_Computing_Lab_1.Repositories.Implementations;

public class InMemoryAuthorRepository : BaseInMemoryRepository<Author>, IAuthorRepository
{
    /*
    // Индекс для поиска по логину
    private readonly Dictionary<string, long> _loginIndex = [];

    public override async Task<Author> CreateAsync(Author entity)
    {
        if (_loginIndex.ContainsKey(entity.Login))
        {
            throw new ConflictException(ErrorCodes.UserAlreadyExists, ErrorMessages.UserAlreadyExists(entity.Login));
        }

        var user = await base.CreateAsync(entity);
        _loginIndex.Add(user.Login, user.Id);

        return user;
    }

    public override async Task<Author?> UpdateAsync(Author entity)
    {
        if (_loginIndex.TryGetValue(entity.Login, out long value) && value != entity.Id)
        {
            throw new ConflictException(ErrorCodes.UserAlreadyExists, ErrorMessages.UserAlreadyExists(entity.Login));
        }

        var updatedUser = await base.UpdateAsync(entity);
        if (updatedUser != null)
        {
            if (_loginIndex.ContainsKey(entity.Login) && _loginIndex[entity.Login] == entity.Id)
            {
                return updatedUser;
            }

            _loginIndex.Remove(entity.Login);
            _loginIndex.Add(updatedUser.Login, updatedUser.Id);
        }

        return updatedUser;
    }

    public override async Task<Author?> DeleteAsync(long id)
    {
        var user = await base.DeleteAsync(id);

        if (user != null)
        {
            _loginIndex.Remove(user.Login);
        }

        return user;
    }
    */
}