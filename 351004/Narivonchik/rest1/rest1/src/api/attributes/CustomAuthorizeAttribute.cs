using Microsoft.AspNetCore.Authorization;

namespace rest1.api.attributes;

public class CustomAuthorizeAttribute : AuthorizeAttribute
{
    public CustomAuthorizeAttribute()
    {
        AuthenticationSchemes = "Bearer";
    }
    
    public CustomAuthorizeAttribute(string policy) : base(policy)
    {
        AuthenticationSchemes = "Bearer";
    }
}