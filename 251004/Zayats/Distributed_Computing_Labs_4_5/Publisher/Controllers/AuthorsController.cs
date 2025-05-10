using Microsoft.AspNetCore.Mvc;
using Publisher.DTO.RequestDTO;
using Publisher.Services.Interfaces;

namespace Publisher.Controllers.V1;

[ApiController]
[Route("api/v1.0/[controller]")]
public class AuthorsController : ControllerBase
{
    private readonly IAuthorService _iAuthorService;

    public AuthorsController(IAuthorService iAuthorService)
    {
        _iAuthorService = iAuthorService;
    }

    [HttpGet]
    public async Task<IActionResult> GetUsers()
    {
        var users = await _iAuthorService.GetUsersAsync();
        return Ok(users);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetUserById(long id)
    {
        var user = await _iAuthorService.GetUserByIdAsync(id);
        return Ok(user);
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser([FromBody] AuthorRequestDTO author)
    {
        var createdUser = await _iAuthorService.CreateUserAsync(author);
        return CreatedAtAction(nameof(CreateUser), new { id = createdUser.Id }, createdUser);
    }
    
    [HttpPut]
    public async Task<IActionResult> UpdateUser([FromBody] AuthorRequestDTO author)
    {
        var updatedUser = await _iAuthorService.UpdateUserAsync(author);
        return Ok(updatedUser);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteUser(long id)
    {
        await _iAuthorService.DeleteUserAsync(id);
        return NoContent();
    }
}