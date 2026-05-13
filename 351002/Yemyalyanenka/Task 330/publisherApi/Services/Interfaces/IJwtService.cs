using RestApiTask.Models.Entities;
using System.Security.Claims;

namespace publisherApi.Services.Interfaces
{
    public interface IJwtService
    {
        string GenerateToken(Writer writer);
        ClaimsPrincipal? ValidateToken(string token);
    }
}
