using System.Net;
using System.Text.Json;
using Redis.Models;

namespace Redis.Middleware;

public class ErrorHandlingMiddleware
{
    private readonly RequestDelegate _next;

    public ErrorHandlingMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context)
    {
        try
        {
            await _next(context);
            
            if (context.Response.StatusCode == (int)HttpStatusCode.Unauthorized)
                await WriteErrorResponse(context, "Authentication failed or token is missing", 40100);
            else if (context.Response.StatusCode == (int)HttpStatusCode.Forbidden)
                await WriteErrorResponse(context, "Access denied. Insufficient permissions.", 40300);
        }
        catch (Exception ex)
        {
            context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
            await WriteErrorResponse(context, ex.Message, 50000);
        }
    }

    private static async Task WriteErrorResponse(HttpContext context, string message, int code)
    {
        if (!context.Response.HasStarted)
        {
            context.Response.ContentType = "application/json";
            var error = new ErrorResponse { errorMessage = message, errorCode = code.ToString() };
            await context.Response.WriteAsync(JsonSerializer.Serialize(error));
        }
    }
}