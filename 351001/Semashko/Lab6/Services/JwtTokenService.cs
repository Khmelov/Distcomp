using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using lab_1.Entities;
using Microsoft.IdentityModel.Tokens;

namespace lab_1.Services;

public class JwtTokenService
{
    private const string Secret = "lab6-super-secret-key-min-32-symbols";
    private static readonly byte[] SecretBytes = Encoding.UTF8.GetBytes(Secret);

    public string GenerateToken(TblAuthor author)
    {
        var credentials = new SigningCredentials(new SymmetricSecurityKey(SecretBytes), SecurityAlgorithms.HmacSha256);
        var now = DateTime.UtcNow;
        var role = string.IsNullOrWhiteSpace(author.Role) ? "CUSTOMER" : author.Role.ToUpperInvariant();
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, author.Login),
            new Claim(JwtRegisteredClaimNames.Iat, new DateTimeOffset(now).ToUnixTimeSeconds().ToString(), ClaimValueTypes.Integer64),
            new Claim(ClaimTypes.Role, role),
            new Claim("role", role)
        };

        var token = new JwtSecurityToken(
            claims: claims,
            notBefore: now,
            expires: now.AddHours(2),
            signingCredentials: credentials);

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public static TokenValidationParameters BuildValidationParameters()
    {
        return new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(SecretBytes),
            ValidateLifetime = true,
            ClockSkew = TimeSpan.FromSeconds(30),
            RoleClaimType = ClaimTypes.Role,
            NameClaimType = JwtRegisteredClaimNames.Sub
        };
    }
}
