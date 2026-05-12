using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers.V2
{
    [ApiController]
    [Route("api/v2.0")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("login")]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
        {
            var token = await _authService.AuthenticateAsync(request.Login, request.Password);
            if (token == null)
                return Unauthorized(new ErrorResponse("Invalid login or password", 40101));

            return Ok(new LoginResponse { AccessToken = token });
        }

        [HttpPost("register")]
        public async Task<ActionResult<LoginResponse>> Register([FromBody] LoginRequest request)
        {
            var token = await _authService.AuthenticateAsync(request.Login, request.Password);
            if (token == null)
                return Unauthorized(new ErrorResponse("Invalid login or password", 40101));

            return Ok(new LoginResponse { AccessToken = token });
        }
    }

    // Вспомогательный класс для ошибок
    public class ErrorResponse
    {
        public string ErrorMessage { get; set; }
        public int ErrorCode { get; set; }

        public ErrorResponse(string message, int code)
        {
            ErrorMessage = message;
            ErrorCode = code;
        }
    }
}