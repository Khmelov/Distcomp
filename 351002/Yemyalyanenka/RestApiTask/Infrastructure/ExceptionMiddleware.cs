using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models;

namespace RestApiTask.Infrastructure;

public class ExceptionMiddleware
{
    private readonly RequestDelegate _next;
    public ExceptionMiddleware(RequestDelegate next) => _next = next;

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);

            // Перехват 404/405, если они не вызвали исключение (защита от HTML)
            if ((context.Response.StatusCode == 404 || context.Response.StatusCode == 405) && !context.Response.HasStarted)
            {
                context.Response.ContentType = "application/json";
                var response = new ErrorResponse("Resource or Method not found", $"{context.Response.StatusCode}00");
                await context.Response.WriteAsJsonAsync(response);
            }
        }
        catch (Exception ex)
        {
            await HandleExceptionAsync(context, ex);
        }
    }

    private static Task HandleExceptionAsync(HttpContext context, Exception ex)
    {
        var (statusCode, subCode) = ex switch
        {
            NotFoundException => (StatusCodes.Status404NotFound, "01"),
            ValidationException => (StatusCodes.Status400BadRequest, "01"),
            _ => (StatusCodes.Status500InternalServerError, "00")
        };

        context.Response.ContentType = "application/json";
        context.Response.StatusCode = statusCode;

        var response = new ErrorResponse(ex.Message, $"{statusCode}{subCode}");
        return context.Response.WriteAsJsonAsync(response);
    }
}
