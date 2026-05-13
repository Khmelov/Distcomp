using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using Publisher.Application;
using Publisher.Domain.Models;

namespace Publisher.Infrastructure;

public class JwtGenerator : IJwtGenerator
{
    public string GetJwt(User user)
    {
        var expTime = DateTime.UtcNow.AddMinutes(15);
        List<Claim> claims =
        [
            new(JwtRegisteredClaimNames.Sub, user.Id.ToString()),
            new(JwtRegisteredClaimNames.Email, user.Login),
            new(ClaimTypes.Role, user.Role),  
        ];

        SecurityKey key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("SuperMegaSecretKeyForJwtTokenValidation"));

        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        
        JwtSecurityToken securityToken = new(
            expires: expTime,
            signingCredentials: creds,
            audience: "localhost:24110",
            issuer: "localhost:24110",
            claims: claims,
            notBefore: DateTime.UtcNow
        );

        return new JwtSecurityTokenHandler().WriteToken(securityToken);
    }
    
}
