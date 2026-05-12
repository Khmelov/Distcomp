using System.Security.Claims;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;

public interface IJwtService
{
    string GenerateToken(string login, string role);
    ClaimsPrincipal? ValidateToken(string token);
    bool IsTokenValid(string token);
    (string? Login, string? Role) GetLoginAndRoleFromToken(string token);
}