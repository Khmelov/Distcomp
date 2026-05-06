using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v2.0")]
public class AuthController : ControllerBase
{
    private readonly IWriterService _writerService;
    private readonly IJwtService _jwtService;

    public AuthController(IWriterService writerService, IJwtService jwtService)
    {
        _writerService = writerService;
        _jwtService = jwtService;
    }

    [HttpPost("login")]
    public IActionResult Login([FromBody] AuthRequest request)
    {
        if (string.IsNullOrEmpty(request.Login) || string.IsNullOrEmpty(request.Password))
        {
            return Unauthorized(new ErrorResponse
            {
                ErrorMessage = "Invalid login or password",
                ErrorCode = 40102
            });
        }

        var writer = _writerService.GetByLogin(request.Login);
        if (writer == null)
        {
            return Unauthorized(new ErrorResponse
            {
                ErrorMessage = "Invalid login or password",
                ErrorCode = 40102
            });
        }

        if (!_writerService.VerifyPassword(request.Password, writer.Password))
        {
            return Unauthorized(new ErrorResponse
            {
                ErrorMessage = "Invalid login or password",
                ErrorCode = 40102
            });
        }

        var token = _jwtService.GenerateToken(writer.Login, writer.Role);

        return Ok(new AuthResponse
        {
            AccessToken = token,
            TokenType = "Bearer"
        });
    }

    [HttpPost("writers")]
    public IActionResult Register([FromBody] WriterRegistrationRequest request)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(new ErrorResponse
            {
                ErrorMessage = "Invalid registration data",
                ErrorCode = 40001
            });
        }

        var writerRequest = new WriterRequestTo
        {
            Login = request.Login,
            Password = request.Password,
            Firstname = request.Firstname,
            Lastname = request.Lastname,
            Role = string.IsNullOrEmpty(request.Role) ? "CUSTOMER" : request.Role
        };

        var created = _writerService.Create(writerRequest);
        return StatusCode(201, created);
    }
}