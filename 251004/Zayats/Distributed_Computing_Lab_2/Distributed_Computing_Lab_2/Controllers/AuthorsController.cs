using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distributed_Computing_Lab_2.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class AuthorsController : ControllerBase
{
    private readonly IAuthorService _authorService;

    public AuthorsController(IAuthorService authorService)
    {
        _authorService = authorService;
    }

    [HttpGet]
    public async Task<IActionResult> GetUsers()
    {
        var users = await _authorService.GetUsersAsync();
        return Ok(users);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetUserById(long id)
    {
        var user = await _authorService.GetUserByIdAsync(id);
        return Ok(user);
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser([FromBody] AuthorRequestDTO author)
    {
        var createdUser = await _authorService.CreateUserAsync(author);
        return CreatedAtAction(nameof(CreateUser), new { id = createdUser.Id }, createdUser);
    }
    
    [HttpPut]
    public async Task<IActionResult> UpdateUser([FromBody] AuthorRequestDTO author)
    {
        var updatedUser = await _authorService.UpdateUserAsync(author);
        return Ok(updatedUser);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteUser(long id)
    {
        await _authorService.DeleteUserAsync(id);
        return NoContent();
    }
}