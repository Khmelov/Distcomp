using RW.Domain.Entities;

namespace RW.Api.Security;

public interface IJwtTokenService
{
    string GenerateToken(Author author, out DateTime expiresAt);
}
