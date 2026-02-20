using Microsoft.AspNetCore.Http.Extensions;

namespace ArticleHouse.Additions;

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