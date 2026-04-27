using Microsoft.AspNetCore.Authorization;

namespace rest1.api.requirements;

public class RoleRequirement : IAuthorizationRequirement
{
    public string Role { get; }

    public RoleRequirement(string role)
    {
        Role = role;
    }
}