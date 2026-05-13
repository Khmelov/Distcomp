using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using BlogService.Application.Interfaces.Services;

namespace BlogService.API.Controllers;

[Route("api/v2.0/login")]
[ApiController]
public class AuthController : ControllerBase
{
    private readonly IUserService<long> _userService;

    public AuthController(IUserService<long> userService)
    {
        _userService = userService;
    }

    [HttpPost]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        var creators = await _userService.GetAllAsync();
        var creator = creators.FirstOrDefault(c => c.Login == request.Login);

        if (creator == null || !BCrypt.Net.BCrypt.Verify(request.Password, creator.Password))
        {
            return Unauthorized(new { error = "Invalid login or password" });
        }

        var tokenHandler = new JwtSecurityTokenHandler();
        // Ключ должен совпадать с тем, что в Program.cs
        var key = Encoding.UTF8.GetBytes("YourSuperSecretKeyThatIsAtLeast32BytesLong!");

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(new[]
            {
                new Claim(ClaimTypes.NameIdentifier, creator.Login), // Это поле sub
                new Claim(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString(),
                    ClaimValueTypes.Integer64), // iat
                new Claim(ClaimTypes.Role, creator.Role) // Поле role (ADMIN или CUSTOMER)
            }),
            Expires = DateTime.UtcNow.AddHours(2), // exp
            SigningCredentials =
                new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
        };

        var token = tokenHandler.CreateToken(tokenDescriptor);

        return Ok(new
        {
            access_token = tokenHandler.WriteToken(token)
        });
    }
}

// Класс для принятия данных логина
public class LoginRequest
{
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}