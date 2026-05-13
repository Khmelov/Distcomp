using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations
{
    public class JwtService : IJwtService
    {
        private readonly IConfiguration _configuration;
        private readonly SymmetricSecurityKey _key;

        public JwtService(IConfiguration configuration)
        {
            _configuration = configuration;
            var secret = configuration["Jwt:Secret"] ?? "super-secret-key-with-minimum-64-characters-long-for-jwt";
            if (secret.Length < 64)
            {
                throw new InvalidOperationException($"JWT Secret must be at least 64 characters. Current length: {secret.Length}");
            }
            _key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret));
        }

        public string GenerateToken(string login, string role)
        {
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.NameIdentifier, login),
                new Claim(ClaimTypes.Name, login),
                new Claim(ClaimTypes.Role, role),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
            };

            var creds = new SigningCredentials(_key, SecurityAlgorithms.HmacSha256);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = DateTime.UtcNow.AddHours(24),
                IssuedAt = DateTime.UtcNow,
                SigningCredentials = creds
            };

            var tokenHandler = new JwtSecurityTokenHandler();
            var token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        public ClaimsPrincipal? ValidateToken(string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();
                var principal = tokenHandler.ValidateToken(token, new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = _key,
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.Zero
                }, out _);

                return principal;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Token validation failed: {ex.Message}");
                return null;
            }
        }

        public bool IsTokenValid(string token)
        {
            return ValidateToken(token) != null;
        }

        public (string? Login, string? Role) GetLoginAndRoleFromToken(string token)
        {
            try
            {
                var tokenHandler = new JwtSecurityTokenHandler();
                var jwtToken = tokenHandler.ReadJwtToken(token);

                //Пробуем разные варианты получения логина
                var login = jwtToken.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value
                    ?? jwtToken.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Name)?.Value
                    ?? jwtToken.Claims.FirstOrDefault(c => c.Type == "sub")?.Value;

                var role = jwtToken.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Role)?.Value;

                return (login, role);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error reading token: {ex.Message}");
                return (null, null);
            }
        }
    }
}