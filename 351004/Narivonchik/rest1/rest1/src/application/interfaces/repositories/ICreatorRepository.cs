using rest1.core.entities;

namespace rest1.application.interfaces;

public interface ICreatorRepository : IRepository<Creator>
{
    Task<Creator?> FindByLoginAsync(string login);
}