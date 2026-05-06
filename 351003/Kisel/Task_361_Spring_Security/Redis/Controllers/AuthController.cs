using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Redis.Data;
using Redis.Models;

namespace Redis.Controllers.V2;

[ApiController]
[Route("api/v2.0")]
public class AuthController : ControllerBase
{
    private readonly PublisherDbContext _context;
    private readonly IConfiguration _config;

    public AuthController(PublisherDbContext context, IConfiguration config)
    {
        _context = context;
        _config = config;
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        var creator = await _context.Creators.FirstOrDefaultAsync(c => c.Login == request.Login);
        
        // Проверяем логин и хэш пароля через BCrypt
        if (creator == null || !BCrypt.Net.BCrypt.Verify(request.Password, creator.Password))
        {
            return Unauthorized(new ErrorResponse { errorMessage = "Invalid login or password", errorCode = "40101" });
        }

        // Генерируем JWT токен
        var tokenHandler = new JwtSecurityTokenHandler();
        var key = Encoding.UTF8.GetBytes(_config["Jwt:Key"]!);
        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, creator.Login),
                new Claim(ClaimTypes.Role, creator.Role)
            }),
            Expires = DateTime.UtcNow.AddHours(2),
            Issuer = _config["Jwt:Issuer"],
            Audience = _config["Jwt:Audience"],
            SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
        };

        var token = tokenHandler.CreateToken(tokenDescriptor);
        return Ok(new LoginResponse { access_token = tokenHandler.WriteToken(token) });
    }
}