using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.Auth;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers.v2;

[ApiController]
[Route("api/v2.0")]
public class AuthController : ControllerBase
{
    private readonly ICreatorService _creatorService;
    private readonly IJwtService _jwtService;
    private readonly ILogger<AuthController> _logger;

    public AuthController(ICreatorService creatorService, IJwtService jwtService, ILogger<AuthController> logger)
    {
        _creatorService = creatorService;
        _jwtService = jwtService;
        _logger = logger;
    }

    [HttpPost("creators")]
    public async Task<ActionResult<CreatorResponseTo>> Register([FromBody] RegisterRequestDto request)
    {
        try
        {
            var creator = await _creatorService.RegisterAsync(request);
            return CreatedAtAction(nameof(GetCreatorById), new { id = creator.Id }, creator);
        }
        catch (ConflictException ex)
        {
            return Conflict(new { errorMessage = ex.Message, errorCode = "40901" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { errorMessage = ex.Message, errorCode = "40001" });
        }
    }

    [HttpGet("creators/{id}")]
    public async Task<ActionResult<CreatorResponseTo>> GetCreatorById(long id)
    {
        var creator = await _creatorService.GetCreatorByIdAsync(id);
        if (creator == null)
            return NotFound(new { errorMessage = "Creator not found", errorCode = "40401" });
        return Ok(creator);
    }

    [HttpPost("login")]
    public async Task<ActionResult<LoginResponseDto>> Login([FromBody] LoginRequestDto request)
    {
        try
        {
            var response = await _creatorService.LoginAsync(request);
            return Ok(response);
        }
        catch (UnauthorizedException)
        {
            return Unauthorized(new { errorMessage = "Invalid login or password", errorCode = "40101" });
        }
    }
}