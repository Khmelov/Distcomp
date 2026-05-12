using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RW.Api.Security;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Infrastructure.Data;

namespace RW.Api.Controllers.V2;

[ApiController]
[Route("api/v2.0")]
[AllowAnonymous]
public class AuthV2Controller : ControllerBase
{
    private readonly ApplicationDbContext _db;
    private readonly IJwtTokenService _jwt;

    public AuthV2Controller(ApplicationDbContext db, IJwtTokenService jwt)
    {
        _db = db;
        _jwt = jwt;
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequestTo dto)
    {
        if (string.IsNullOrEmpty(dto.Login) || string.IsNullOrEmpty(dto.Password))
            throw new UnauthorizedException("Login and password are required.");

        var author = await _db.Authors.AsNoTracking()
            .FirstOrDefaultAsync(a => a.Login == dto.Login);

        if (author is null)
            throw new UnauthorizedException("Invalid login or password.");

        bool ok;
        try
        {
            ok = BCrypt.Net.BCrypt.Verify(dto.Password, author.Password);
        }
        catch
        {
            ok = author.Password == dto.Password;
        }

        if (!ok)
            throw new UnauthorizedException("Invalid login or password.");

        var token = _jwt.GenerateToken(author, out var expiresAt);

        return Ok(new LoginResponseTo
        {
            AccessToken = token,
            TokenType = "Bearer",
            ExpiresAt = expiresAt
        });
    }
}
