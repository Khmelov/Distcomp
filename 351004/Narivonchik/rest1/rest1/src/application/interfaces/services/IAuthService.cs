using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;

namespace rest1.application.interfaces.services;

public interface IAuthService
{
    Task<LoginResponseTo> AuthenticateAsync(LoginRequestTo loginRequest);
    Task<UserInfoResponseTo> GetCurrentUserAsync(string login);
    Task<bool> ValidateTokenAsync(string token);
    string? GetLoginFromToken(string token);
    string? GetRoleFromToken(string token);
}