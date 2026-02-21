using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Domain.Entities;

namespace BlogService.Application.Mappers;

public class UserMapper : IRequestMapper<UserRequestToDto, User>, IResponseMapper<User, UserResponseToDto>
{
    public User Map(UserRequestToDto dto)
    {
        return new User()
        {
            ID = dto.ID,
            Login = dto.Login,
            Password = dto.Password,
            FirstName = dto.FirstName,
            LastName = dto.LastName,
        };
    }

    public UserResponseToDto Map(User entity)
    {
        return new UserResponseToDto()
        {
            ID = entity.ID,
            Login = entity.Login,
            Password = entity.Password,
            FirstName = entity.FirstName,
            LastName = entity.LastName,
        };
    }
}