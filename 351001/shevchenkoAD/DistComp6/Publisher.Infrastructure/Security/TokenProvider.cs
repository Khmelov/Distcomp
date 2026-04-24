using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using JwtRegisteredClaimNames = System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames;

namespace Publisher.Infrastructure.Security;

public class TokenProvider : ITokenProvider
{
    private readonly IConfiguration _config;

    public TokenProvider(IConfiguration config)
    {
        _config = config;
    }

    public string GenerateToken(Author author)
    {
        var claims = new List<Claim>
        {
            new(ClaimTypes.Name, author.Login),


            new(ClaimTypes.Role, author.Role.ToString()),


            new("userId", author.Id.ToString()),

            new(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString(),
                ClaimValueTypes.Integer64)
        };

        var keyString = _config["Jwt:Key"] ?? "SecretKeyMustBeVeryLongAtLeast32Chars!!";
        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(keyString));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            _config["Jwt:Issuer"],
            _config["Jwt:Audience"],
            claims,
            expires: DateTime.UtcNow.AddHours(3),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}