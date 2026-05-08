using rest1.application.interfaces;
using rest1.core.entities;

namespace rest1.infrastructure.persistence;

public class CreatorRepository : Repository<Creator>, ICreatorRepository
{
    public async Task<Creator?> FindByLoginAsync(string login)
    {
        throw new NotImplementedException();
    }
}