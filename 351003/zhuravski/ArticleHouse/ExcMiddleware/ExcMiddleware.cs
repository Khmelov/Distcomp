using System.ComponentModel.DataAnnotations;
using ArticleHouse.Service.Exceptions;

namespace ArticleHouse.ExcMiddleware;

public class ExcMiddleware
{
    private readonly RequestDelegate next;

    public ExcMiddleware(RequestDelegate next)
    {
        this.next = next;
    }

    static private async Task HandleException(HttpContext context, Exception e, int code)
    {
        context.Response.StatusCode = code;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(new {
            error = e.Message
        });
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try {
            await next(context);
        }
        catch (ServiceObjectNotFoundException e)
        {
            await HandleException(context, e, StatusCodes.Status404NotFound);
        }
        catch (ServiceException e)
        {
            await HandleException(context, e, StatusCodes.Status400BadRequest);
        }
        catch (ValidationException e)
        {
            await HandleException(context, e, StatusCodes.Status400BadRequest);
        }
    }
}