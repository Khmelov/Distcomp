using System.Text.Json;
using Publisher.Application.Exceptions;
using Shared.DTOs.Responses;

namespace Publisher.Presentation.Middleware;

public class ExceptionHandlingMiddleware
{
    private readonly RequestDelegate _next;

    public ExceptionHandlingMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);

            if (context.Response.StatusCode == 401 && !context.Response.HasStarted)
                await WriteErrorResponse(context, 401, 00, "Unauthorized: Access token is missing or invalid");
            else if (context.Response.StatusCode == 403 && !context.Response.HasStarted)
                await WriteErrorResponse(context, 403, 00, "Forbidden: You don't have permission for this resource");
        }
        catch (RestException ex)
        {
            context.Response.ContentType = "application/json";
            context.Response.StatusCode = ex.StatusCode;

            var fullErrorCode = ex.StatusCode * 100 + ex.SubCode;

            var response = new ErrorResponse(ex.Message, fullErrorCode);

            var json = JsonSerializer.Serialize(response);
            await context.Response.WriteAsync(json);
        }
        catch (Exception ex)
        {
            context.Response.ContentType = "application/json";
            context.Response.StatusCode = 500;

            var response = new ErrorResponse($"An unexpected error occurred: {ex.Message}", 50000);

            var json = JsonSerializer.Serialize(response);
            await context.Response.WriteAsync(json);
        }
    }

    private async Task WriteErrorResponse(HttpContext context, int status, int sub, string msg)
    {
        context.Response.ContentType = "application/json";
        context.Response.StatusCode = status;
        var response = new ErrorResponse(msg, status * 100 + sub);
        await context.Response.WriteAsJsonAsync(response);
    }
}