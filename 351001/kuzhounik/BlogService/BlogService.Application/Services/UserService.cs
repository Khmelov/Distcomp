using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using BlogService.Domain.Interfaces;

namespace BlogService.Application.Services;

public class UserService : BaseService<long, User, UserRequestToDto, UserResponseToDto>, IUserService
{
    public UserService(IRepository<long, User> repository,
        IRequestMapper<UserRequestToDto, User> userRequestMapper,
        IResponseMapper<User, UserResponseToDto> userResponseMapper) : base(repository, userRequestMapper, userResponseMapper){ }
}