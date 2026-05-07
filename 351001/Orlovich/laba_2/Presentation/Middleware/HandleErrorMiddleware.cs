using System.Net;
using System.Text.Json;

namespace Dc.Middleware;

public class HandleErrorMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<HandleErrorMiddleware> _logger;
    private readonly IHostEnvironment _env;

    public HandleErrorMiddleware(RequestDelegate next, ILogger<HandleErrorMiddleware> logger, IHostEnvironment env)
    {
        _next = next;
        _logger = logger;
        _env = env;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, ex.Message);
            await HandleExceptionAsync(context, ex);
        }
    }

    private async Task HandleExceptionAsync(HttpContext context, Exception exception)
    {
        context.Response.ContentType = "application/json";
        
        var statusCode = exception switch
        {
            _ => HttpStatusCode.InternalServerError             // 500
        };

        context.Response.StatusCode = (int)statusCode;

        var response = new ErrorResponse
        {
            StatusCode = context.Response.StatusCode,
            Message = exception.Message,
            Details = exception.StackTrace 
        };

        var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
        var json = JsonSerializer.Serialize(response, options);

        await context.Response.WriteAsync(json);
    }
}