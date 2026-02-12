using Distcomp.Application.DTOs;

namespace Distcomp.Application.Interfaces
{

    public interface IUserService
    {
        UserResponseTo Create(UserRequestTo request);
        UserResponseTo? GetById(long id);
        IEnumerable<UserResponseTo> GetAll();
        UserResponseTo Update(long id, UserRequestTo request);
        bool Delete(long id);
    }
}