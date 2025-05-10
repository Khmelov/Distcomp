using System.Net;
using Discussion.Exceptions;

namespace Discussion.Middleware;

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