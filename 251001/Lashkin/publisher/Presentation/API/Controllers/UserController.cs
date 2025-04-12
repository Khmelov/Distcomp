using Application.DTO.Request;
using Application.Features.Users.Commands;
using Application.Features.Users.Queries;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/v1.0/users")]
public class UserController : ControllerBase
{
    private readonly ISender _sender;

    public UserController(ISender sender)
    {
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetUsers()
    {
        var usersResponseTo = await _sender.Send(new ReadUsersQuery());

        return Ok(usersResponseTo);
    }

    [HttpGet("{id:long}", Name = "GetUserById")]
    public async Task<IActionResult> GetUserById([FromRoute] long id)
    {
        var userResponseTo = await _sender.Send(new ReadUserQuery(id));

        return Ok(userResponseTo);
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser([FromBody] UserRequestTo userRequestTo)
    {
        var userResponseTo = await _sender.Send(new CreateUserCommand(userRequestTo));

        return CreatedAtRoute(nameof(GetUserById), new { id = userResponseTo.Id }, userResponseTo);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateUser([FromBody] UpdateUserRequestTo updateUserRequestTo)
    {
       var userRequestTo = new UserRequestTo(
            updateUserRequestTo.Login,
            updateUserRequestTo.Password,
            updateUserRequestTo.FirstName,
            updateUserRequestTo.LastName);

        var userResponseTo = await _sender.Send(new UpdateUserCommand(updateUserRequestTo.Id, userRequestTo));

        return Ok(userResponseTo);
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateUserById([FromRoute] long id, [FromBody] UserRequestTo userRequestTo)
    {
        var userResponseTo = await _sender.Send(new UpdateUserCommand(id, userRequestTo));

        return Ok(userResponseTo);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteUser([FromRoute] long id)
    {
        await _sender.Send(new DeleteUserCommand(id));

        return NoContent();
    }
}