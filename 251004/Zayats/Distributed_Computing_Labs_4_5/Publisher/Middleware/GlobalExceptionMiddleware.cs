using System.ComponentModel.DataAnnotations;
using System.Net;
using Microsoft.EntityFrameworkCore;
using Publisher.Exceptions;

namespace Publisher.Middleware;

public class GlobalExceptionMiddleware
{
    private readonly RequestDelegate _next;
    public GlobalExceptionMiddleware(RequestDelegate next)
    {
        _next = next;
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

    private async Task HandleExceptionAsync(HttpContext context, Exception ex)
    {
        context.Response.ContentType = "application/json";
        switch (ex)
        {
            case FluentValidation.ValidationException validationException:
            {
                context.Response.StatusCode = (int)HttpStatusCode.BadRequest;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorCode = (int)HttpStatusCode.BadRequest,
                    errorMessage = validationException.Message,
                    errors = validationException.Errors
                });
                break;
            }
            case DbUpdateException dbUpdateException:
            {
                context.Response.StatusCode = (int)HttpStatusCode.Forbidden;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorCode = (int)HttpStatusCode.Forbidden,
                    errorMessage = dbUpdateException.Message,
                    error = dbUpdateException?.InnerException?.Message
                });
                break;
            }
            case NotFoundException notFoundException:
            {
                context.Response.StatusCode = (int)HttpStatusCode.NotFound;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorCode = notFoundException.ErrorCode,
                    errorMessage = notFoundException.Message
                });
                break;
            }
            case HttpRequestException httpRequestException:
            {
                context.Response.StatusCode = (int)HttpStatusCode.BadRequest;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorCode = (int)HttpStatusCode.BadRequest,
                    errorMessage = httpRequestException.Message
                });
                break;
            }
            default:
            {
                context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorCode = (int)HttpStatusCode.InternalServerError,
                    errorMessage = ex.Message,
                });
                break;
            }
        }
    }
    
}