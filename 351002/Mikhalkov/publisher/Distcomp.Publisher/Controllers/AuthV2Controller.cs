using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v2.0")]
public class AuthV2Controller : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthV2Controller(IAuthService authService)
    {
        _authService = authService;
    }

    [HttpPost("login")] 
    public IActionResult Login([FromBody] AuthRequestTo request)
    {
        var response = _authService.Login(request);
        return Ok(response);
    }
}