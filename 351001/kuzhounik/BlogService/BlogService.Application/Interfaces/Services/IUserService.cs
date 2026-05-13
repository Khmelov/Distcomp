using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using Shared.Application.Interfaces.Services;

namespace BlogService.Application.Interfaces.Services;

public interface IUserService<Id> : IService<Id, UserRequestToDto<Id>, UserResponseToDto<Id>>
{
    
}