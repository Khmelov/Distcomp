using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers.v2;

[ApiController]
[Route("api/v2.0")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;
    private readonly ILogger<AuthController> _logger;

    public AuthController(IAuthService authService, ILogger<AuthController> logger)
    {
        _authService = authService;
        _logger = logger;
    }

    // register - вынесен в creator api 
    
    // login 
    [HttpPost("login")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(LoginResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<ActionResult<LoginResponseTo>> Login([FromBody] LoginRequestTo loginRequest)
    {
        try
        {
            _logger.LogInformation("Login attempt for user: {Login}", loginRequest.Login);
            var response = await _authService.AuthenticateAsync(loginRequest);
            return Ok(response);
        }
        catch (UnauthorizedException ex)
        {
            _logger.LogWarning(ex, "Login failed for user: {Login}", loginRequest.Login);
            return Unauthorized(new { errorMessage = ex.Message, errorCode = "40101" });
        }
    }

    // get current user 
    [HttpGet("me")]
    [Authorize(AuthenticationSchemes = "Bearer")]
    [ProducesResponseType(typeof(UserInfoResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<UserInfoResponseTo>> GetCurrentUser()
    {
        try
        {
            var login = User.FindFirst(ClaimTypes.NameIdentifier)?.Value 
                        ?? User.FindFirst(JwtRegisteredClaimNames.Sub)?.Value;
            
            if (string.IsNullOrEmpty(login))
            {
                return Unauthorized(new { errorMessage = "Invalid token", errorCode = "40102" });
            }

            var user = await _authService.GetCurrentUserAsync(login);
            return Ok(user);
        }
        catch (NewNotFoundException ex)
        {
            return NotFound(new { errorMessage = ex.Message, errorCode = "40401" });
        }
    }
}