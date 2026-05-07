using System.ComponentModel.DataAnnotations;
using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;

namespace BlogService.API.Controllers;

[ApiController]
[Route("api/v1.0/users")]
public class UserController : BaseController<UserRequestToDto, UserResponseToDto>
{
    public UserController(IUserService userService) : base(userService) { }
}