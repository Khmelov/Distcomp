using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

[ApiController]
[Route("api/v2.0/writers")]
[Authorize]
public class WriterV2Controller : ControllerBase
{
    private readonly IWriterService _service;

    public WriterV2Controller(IWriterService service)
    {
        _service = service;
    }

    private bool IsAdmin()
    {
        return User.IsInRole("ADMIN");
    }

    private string GetCurrentUserLogin()
    {
        // Try multiple claim types to find the login
        return User.FindFirst(ClaimTypes.NameIdentifier)?.Value 
            ?? User.FindFirst("sub")?.Value 
            ?? "";
    }

    [HttpGet]
    public IActionResult GetAll()
    {
        var writers = _service.GetAll();
        return Ok(writers);
    }

    [HttpGet("{id}")]
    public IActionResult GetById(long id)
    {
        var writer = _service.GetById(id);
        return Ok(writer);
    }

    [HttpPut]
    public IActionResult Update([FromBody] WriterRequestTo request)
    {
        if (!IsAdmin())
        {
            return StatusCode(403, new ErrorResponse
            {
                ErrorMessage = "Forbidden - Admin only",
                ErrorCode = 40301
            });
        }

        if (!ModelState.IsValid)
        {
            return BadRequest(new ErrorResponse
            {
                ErrorMessage = "Invalid data",
                ErrorCode = 40001
            });
        }

        var updated = _service.Update(request);
        return Ok(updated);
    }

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        if (!IsAdmin())
        {
            return StatusCode(403, new ErrorResponse
            {
                ErrorMessage = "Forbidden - Admin only",
                ErrorCode = 40301
            });
        }

        _service.Delete(id);
        return NoContent();
    }
}