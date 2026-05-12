using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;
using Shared.Application.Interfaces.Mappers;

namespace BlogService.Application.Mappers;

public class UserMapper<Id> : IRequestMapper<UserRequestToDto<Id>, User<Id>>, IResponseMapper<User<Id>, UserResponseToDto<Id>>
{
    public User<Id> Map(UserRequestToDto<Id> dto)
    {
        return new User<Id>()
        {
            ID = dto.ID,
            Login = dto.Login,
            Password = dto.Password,
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            Role = dto.Role,
        };
    }

    public UserResponseToDto<Id> Map(User<Id> entity)
    {
        return new UserResponseToDto<Id>()
        {
            ID = entity.ID,
            Login = entity.Login,
            Password = entity.Password,
            FirstName = entity.FirstName,
            LastName = entity.LastName,
            Role = entity.Role
        };
    }
}