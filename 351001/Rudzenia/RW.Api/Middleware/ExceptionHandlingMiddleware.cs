using System.Net;
using System.Text.Json;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using RW.Application.Exceptions;

namespace RW.Api.Middleware;

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

            // Handle 401 / 403 produced by the auth pipeline so they conform to errorCode/errorMessage shape
            if (!context.Response.HasStarted &&
                (context.Response.StatusCode == (int)HttpStatusCode.Unauthorized ||
                 context.Response.StatusCode == (int)HttpStatusCode.Forbidden) &&
                context.Response.ContentLength is null or 0)
            {
                var status = context.Response.StatusCode;
                var message = status == 401
                    ? "Authentication required or token invalid."
                    : "Access denied: insufficient permissions.";
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsync(JsonSerializer.Serialize(new
                {
                    errorCode = status * 100 + 1,
                    errorMessage = message
                }));
            }
        }
        catch (NotFoundException ex)
        {
            context.Response.StatusCode = (int)HttpStatusCode.NotFound;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 40400,
                errorMessage = ex.Message
            }));
        }
        catch (UnauthorizedException ex)
        {
            context.Response.StatusCode = (int)HttpStatusCode.Unauthorized;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 40101,
                errorMessage = ex.Message
            }));
        }
        catch (ForbiddenException ex)
        {
            context.Response.StatusCode = (int)HttpStatusCode.Forbidden;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 40301,
                errorMessage = ex.Message
            }));
        }
        catch (Application.Exceptions.ValidationException ex)
        {
            context.Response.StatusCode = (int)HttpStatusCode.Forbidden;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 40300,
                errorMessage = ex.Message
            }));
        }
        catch (DbUpdateException ex) when (ex.InnerException is PostgresException { SqlState: "23505" })
        {
            context.Response.StatusCode = (int)HttpStatusCode.Forbidden;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 40302,
                errorMessage = "Duplicate entry violates unique constraint."
            }));
        }
        catch (Exception)
        {
            context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
            context.Response.ContentType = "application/json";
            await context.Response.WriteAsync(JsonSerializer.Serialize(new
            {
                errorCode = 50000,
                errorMessage = "An unexpected error occurred."
            }));
        }
    }
}
