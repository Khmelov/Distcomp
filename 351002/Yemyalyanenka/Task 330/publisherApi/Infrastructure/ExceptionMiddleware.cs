using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Npgsql;
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
        var (statusCode, subCode, message) = ex switch
        {
            NotFoundException => (StatusCodes.Status404NotFound, "01", ex.Message),
            ForbiddenException => (StatusCodes.Status403Forbidden, "01", ex.Message),
            ValidationException => (StatusCodes.Status400BadRequest, "01", ex.Message),
            UnauthorizedException => (StatusCodes.Status401Unauthorized, "01", ex.Message),
            InvalidTokenException => (StatusCodes.Status401Unauthorized, "03", ex.Message),
            SecurityTokenException => (StatusCodes.Status401Unauthorized, "02", "Token expired or invalid"),
            UnauthorizedAccessException => (StatusCodes.Status401Unauthorized, "01", ex.Message),
            DbUpdateException dbEx when IsUniqueViolation(dbEx) =>
                (StatusCodes.Status403Forbidden, "01", "Entity already exists"),
            DbUpdateException dbEx when IsForeignKeyViolation(dbEx) =>
                (StatusCodes.Status403Forbidden, "01", "Invalid reference"),
            _ => (StatusCodes.Status500InternalServerError, "00", ex.Message)
        };

        context.Response.ContentType = "application/json";
        context.Response.StatusCode = statusCode;

        var response = new ErrorResponse(message, $"{statusCode}{subCode}");
        return context.Response.WriteAsJsonAsync(response);
    }

    private static bool IsUniqueViolation(DbUpdateException exception) =>
        FindPostgresException(exception)?.SqlState == PostgresErrorCodes.UniqueViolation;

    private static bool IsForeignKeyViolation(DbUpdateException exception) =>
        FindPostgresException(exception)?.SqlState == PostgresErrorCodes.ForeignKeyViolation;

    private static PostgresException? FindPostgresException(Exception? ex)
    {
        while (ex is not null)
        {
            if (ex is PostgresException pgEx) return pgEx;
            ex = ex.InnerException;
        }

        return null;
    }
}
