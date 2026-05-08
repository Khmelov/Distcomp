using Microsoft.AspNetCore.Authorization;
using rest1.api.requirements;
using System.Security.Claims;

namespace rest1.api.handlers;

public class RoleHandler : AuthorizationHandler<RoleRequirement>
{
    protected override Task HandleRequirementAsync(
        AuthorizationHandlerContext context, 
        RoleRequirement requirement)
    {
        var roleClaim = context.User.Claims
            .FirstOrDefault(c => c.Type == ClaimTypes.Role || c.Type == "role");

        if (roleClaim != null && roleClaim.Value == requirement.Role)
        {
            context.Succeed(requirement);
        }
        
        return Task.CompletedTask;
    }
}

public class OwnerOrAdminHandler : AuthorizationHandler<OwnerOrAdminRequirement, long>
{
    private readonly IHttpContextAccessor _httpContextAccessor;

    public OwnerOrAdminHandler(IHttpContextAccessor httpContextAccessor)
    {
        _httpContextAccessor = httpContextAccessor;
    }

    protected override Task HandleRequirementAsync(
        AuthorizationHandlerContext context,
        OwnerOrAdminRequirement requirement,
        long resourceId)
    {
        var userRole = context.User.Claims
            .FirstOrDefault(c => c.Type == ClaimTypes.Role || c.Type == "role")?.Value;

        if (userRole == "ADMIN")
        {
            context.Succeed(requirement);
            return Task.CompletedTask;
        }

        var userLogin = context.User.FindFirst(ClaimTypes.NameIdentifier)?.Value 
                        ?? context.User.FindFirst("sub")?.Value;
        
        context.Succeed(requirement);
        return Task.CompletedTask;
    }
}

public class OwnerOrAdminRequirement : IAuthorizationRequirement { }