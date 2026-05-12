using System.ComponentModel.DataAnnotations;
using Additions.Service;
using Microsoft.AspNetCore.Http;

namespace Additions;

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
        catch (ServiceException e)
        {
            await HandleException(context, e, e.Code);
        }
        catch (ValidationException e)
        {
            await HandleException(context, e, StatusCodes.Status400BadRequest);
        }
        catch (BadHttpRequestException e)
        {
            await HandleException(context, e, StatusCodes.Status400BadRequest);
        }
    }
}