using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers;

[ApiController]
[Route("api/v2.0/login")]
public class AuthController : ControllerBase
{
    private readonly AuthService _authService;

    public AuthController(AuthService authService)
    {
        _authService = authService;
    }

    [HttpPost]
    [AllowAnonymous]
    public async Task<ActionResult<AuthTokenResponseDto>> Login([FromBody] LoginRequestDto dto)
    {
        try
        {
            var token = await _authService.LoginAsync(dto);
            return Ok(new AuthTokenResponseDto { AccessToken = token });
        }
        catch (UnauthorizedAccessException ex)
        {
            return Unauthorized(new ErrorResponseDto
            {
                ErrorMessage = ex.Message,
                ErrorCode = "40101"
            });
        }
    }
}
