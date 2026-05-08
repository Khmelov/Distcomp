using System.Security.Claims;

namespace rest1.application.interfaces.services;

public interface ITokenGenerator
{
    string GenerateToken(string login, string role);
    bool ValidateToken(string token);
    ClaimsPrincipal? GetPrincipalFromToken(string token);
}