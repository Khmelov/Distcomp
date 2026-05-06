using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using publisherApi.Models.DTOs;
using publisherApi.Services.Interfaces;
using RestApiTask.Models.DTOs;

namespace publisherApi.Controllers
{
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

        [HttpPost("login")]
        [AllowAnonymous]
        public async Task<ActionResult<LoginResponseTo>> Login([FromBody] LoginRequestTo request)
        {
            _logger.LogInformation($"Login request for user: {request.Login}");
            var result = await _authService.LoginAsync(request);
            return Ok(result);
        }

        [HttpPost("writers")]
        [AllowAnonymous]
        public async Task<ActionResult<WriterResponseTo>> Register([FromBody] RegisterRequestTo request)
        {
            _logger.LogInformation($"Registration request for user: {request.Login}");
            var result = await _authService.RegisterAsync(request);
            return StatusCode(201, result);
        }

        [HttpGet("me")]
        [Authorize]
        public ActionResult<object> GetCurrentUser()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var login = User.FindFirst(ClaimTypes.Name)?.Value;
            var role = User.FindFirst(ClaimTypes.Role)?.Value;
            var firstname = User.FindFirst("firstname")?.Value;
            var lastname = User.FindFirst("lastname")?.Value;

            if (string.IsNullOrEmpty(userId))
                return Unauthorized(new { message = "User not found in token" });

            return Ok(new
            {
                id = long.Parse(userId),
                login = login,
                firstname = firstname,
                lastname = lastname,
                role = role
            });
        }
    }
}