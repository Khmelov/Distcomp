using Application.Features.User.Commands;
using Application.Features.User.Queries;
using Asp.Versioning;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Routing;
using SocialNet.Abstractions;

namespace SocialNet.Controllers;

[Route("users")]
[ApiVersion("1.0")]
public class UserController : MediatrController
{
    public UserController(IMediator mediator) : base(mediator)
    {
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser(CreateUserCommand command){
        var result = await _mediator.Send(command);

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status201Created, result.Value);
    }

    [HttpGet]
    public async Task<IActionResult> GetUsers(){
        var result = await _mediator.Send(new GetUsersQuery());

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetUserById(long id){
        var result = await _mediator.Send(new GetUserByIdQuery((id)));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateUser(UpdateUserCommand command){
        var result = await _mediator.Send(command);
        
        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteUser(long id){
        var result = await _mediator.Send(new DeleteUserCommand(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status204NoContent);
    }

}
