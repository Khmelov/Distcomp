using Microsoft.AspNetCore.Mvc;
using rest_api;
using rest_api.Dtos;
using rest_api.Services;

[ApiController]
[Route("api/v1.0/users")]
public class UsersController : ControllerBase
{
    private readonly IService<User, UserRequestTo, UserResponseTo> _userService;

    public UsersController(IService<User, UserRequestTo, UserResponseTo> userService)
    {
        _userService = userService;
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<UserResponseTo>> GetById(long id)
    {
        var user = await _userService.GetByIdAsync(id);
        if (user == null)
            return NotFound(new { error = $"User with id {id} not found" });
        return Ok(user);
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<UserResponseTo>>> GetAll()
    {
        var users = await _userService.GetAllAsync();
        return Ok(users);
    }

    [HttpPost]
    public async Task<ActionResult<UserResponseTo>> Create(UserRequestTo request)
    {
        try
        {
            var created = await _userService.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(403, new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(400, new { error = "Internal server error" });
        }
    }

    [HttpPut] 
    public async Task<ActionResult<UserResponseTo>> Update(UserRequestTo request)
    {
        try
        {
            var id = request.Id;
            var updated = await _userService.UpdateAsync(id, request);
            return Ok(updated);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _userService.DeleteAsync(id);
            return NoContent();
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }
}