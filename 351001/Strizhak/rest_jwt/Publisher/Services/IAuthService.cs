using Microsoft.AspNetCore.Identity.Data;
using Publisher.Dtos;
using Publisher.Entities;
using System.Security.Claims;

namespace Publisher.Services
{
    public interface IAuthService
    {
        Task<UserResponseTo> RegisterAsync(UserRequestTo request);
        Task<string> LoginAsync(LoginRequestTo request);
        Task<ClaimsPrincipal?> VerifyTokenAsync(string token);
        Task<User?> GetUserFromTokenAsync(string token);
        Task<string?> GetRoleFromTokenAsync(string token);
        bool CanRead(ClaimsPrincipal userPrincipal, long? resourceOwnerId = null, long? currentUserId = null);
        bool CanModify(ClaimsPrincipal userPrincipal, long resourceOwnerId, long currentUserId);
        Task<User> GetCurrentUserAsync(HttpContext httpContext);
    }
}