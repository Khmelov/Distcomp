using System.Text.Json;
using Discussion.src.NewsPortal.Discussion.Application.Dtos;
using Discussion.src.NewsPortal.Discussion.Domain.Exceptions;

namespace Discussion.src.NewsPortal.Discussion.API.Middleware;

public class ExceptionHandlingMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<ExceptionHandlingMiddleware> _logger;

    public ExceptionHandlingMiddleware(RequestDelegate next, ILogger<ExceptionHandlingMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (Exception ex)
        {
            await HandleExceptionAsync(context, ex);
        }
    }

    private async Task HandleExceptionAsync(HttpContext context, Exception exception)
    {
        _logger.LogError(exception, "An error occurred");

        var response = context.Response;
        response.ContentType = "application/json";

        var errorResponse = exception switch
        {
            ApiException apiEx => new ErrorResponse(apiEx.Message, apiEx.ErrorCode),
            InvalidOperationException invalidOpEx => new ErrorResponse(
                invalidOpEx.Message,
                GenerateErrorCode(409, invalidOpEx.Message)),
            KeyNotFoundException _ => new ErrorResponse(
                "Resource not found",
                "40401"),
            ArgumentException argEx => new ErrorResponse(
                argEx.Message,
                GenerateErrorCode(400, argEx.Message)),
            _ => new ErrorResponse(
                "An internal server error occurred",
                "50000")
        };

        response.StatusCode = exception switch
        {
            ApiException apiEx => apiEx.StatusCode,
            InvalidOperationException => 409,
            KeyNotFoundException => 404,
            ArgumentException => 400,
            _ => 500
        };

        var jsonResponse = JsonSerializer.Serialize(errorResponse);
        await response.WriteAsync(jsonResponse);
    }

    private static string GenerateErrorCode(int statusCode, string message)
    {
        var hash = Math.Abs(message.GetHashCode()) % 100;
        return $"{statusCode}{hash:D2}";
    }
}