using System.Net;
using System.Text.Json;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Shared.Domain.Exceptions;

namespace Shared.Controllers.Middleware;

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
            AlreadyExistsException => HttpStatusCode.Forbidden,
            NotFoundException => HttpStatusCode.NotFound,
            KeyNotFoundException => HttpStatusCode.NotFound,
            _ => HttpStatusCode.InternalServerError
        };

        context.Response.StatusCode = (int)statusCode;

        var response = new ErrorCode
        {
            StatusCode = context.Response.StatusCode,
            Message = exception.Message,
            Details = _env.IsDevelopment() ? exception.StackTrace : null 
        };

        var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
        var json = JsonSerializer.Serialize(response, options);

        await context.Response.WriteAsync(json);
    }
}