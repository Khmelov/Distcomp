using Publisher.Entities;
using System.Security.Claims;

namespace Publisher.Services
{
    public interface ITokenService
    {
        string GenerateToken(User user);

        Task<ClaimsPrincipal?> ValidateTokenAsync(string token);
    }
}