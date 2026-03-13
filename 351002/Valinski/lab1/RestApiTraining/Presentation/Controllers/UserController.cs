using Application.Abstractions;
using Application.Dtos;
using AutoMapper;
using FluentValidation;
using Microsoft.AspNetCore.Mvc;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Controllers;

[ApiController]
[Route("api/v1.0/users")]
public class UserController : ControllerBase
{
    private readonly IUserService _userService;
    private readonly IMapper _mapper;
    private readonly IValidator<UserRequestTo> _userCreateValidator;
    private readonly IValidator<UserUpdateRequestTo> _userUpdateValidator;

    public UserController(IUserService userService, IMapper mapper, IValidator<UserRequestTo> userCreateValidator, IValidator<UserUpdateRequestTo> userUpdateValidator)
    {
        _userService = userService;
        _mapper = mapper;
        _userCreateValidator = userCreateValidator;
        _userUpdateValidator = userUpdateValidator;
    }

    [HttpGet]
    public async Task<ActionResult<List<UserResponseTo>>> GetAllUsers()
    {
        var users = await _userService.GetAllUsers();
        var usersRes = _mapper.Map<List<UserResponseTo>>(users);
        return Ok(usersRes);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<UserResponseTo>> GetUserById(long id)
    {
        var userFromRepo = await _userService.GetUserById(id);
        if (userFromRepo is null)
        {
            return NotFound();
        }

        var userRes = _mapper.Map<UserResponseTo>(userFromRepo);
        return Ok(userRes);
    }

    [HttpPost]
    public async Task<ActionResult<UserResponseTo>> AddUser([FromBody] UserRequestTo userRequest)
    {
        var validationResult = _userCreateValidator.Validate(userRequest);

        if (!validationResult.IsValid)
        {
            return BadRequest(new {message = "validation error"});
        }
        
        var userDto = _mapper.Map<UserCreateDto>(userRequest);
        var res = await _userService.CreateUserAsync(userDto);
        if (res == null)
            return StatusCode(403);
        
        return CreatedAtAction(nameof(GetUserById), new { id = res.Id }, _mapper.Map<UserResponseTo>(res));
    }

    [HttpPut]
    public async Task<ActionResult<UserGetDto>> UpdateUser([FromBody] UserUpdateRequestTo userUpdateRequest)
    {
        var validationResult = _userUpdateValidator.Validate(userUpdateRequest);
        
        if (!validationResult.IsValid)
        {
            return BadRequest(new {message = "Validation error"});
        }
        
        var userToUpdate = _mapper.Map<UserUpdateDto>(userUpdateRequest);
        var updatedUser = await _userService.UpdateUserAsync(userToUpdate);
        if (updatedUser is null)
        {
            return NotFound();
        }

        return Ok(updatedUser);
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> DeleteUser(long id)
    {
        var res = await _userService.DeleteUserAsync(id);
        if (res == false)
        {
            return NotFound();
        }

        return NoContent();
    }
}
