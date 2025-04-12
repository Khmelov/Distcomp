using Application.Exceptions;

namespace API.Middlewares;

public class ExceptionHandlingMiddleware : IMiddleware
{
    public async Task InvokeAsync(HttpContext context, RequestDelegate next)
    {
        try
        {
            await next(context);
        }
        catch (Exception exception)
        {
            var errorMessage = exception.Message;
            var errorCode = exception switch
            {
                NotFoundException => StatusCodes.Status404NotFound,
                AlreadyExistsException => StatusCodes.Status403Forbidden,
                _ => StatusCodes.Status500InternalServerError
            };
            
            context.Response.ContentType = "application/json";
            context.Response.StatusCode = errorCode;

            await context.Response.WriteAsJsonAsync(new
            {
                errorMessage,
                errorCode
            });
        }
    }
}