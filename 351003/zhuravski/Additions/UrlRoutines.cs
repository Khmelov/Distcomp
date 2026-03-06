using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Extensions;

namespace Additions;

public static class UrlRoutines
{
    public static string BuildAbsoluteUrl(HttpContext context, string path)
    {
        return UriHelper.BuildAbsolute(
            context.Request.Scheme,
            context.Request.Host,
            context.Request.PathBase,
            path
        );
    }
}