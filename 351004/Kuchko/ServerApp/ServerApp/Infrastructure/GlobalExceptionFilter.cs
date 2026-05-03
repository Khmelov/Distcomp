using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using ServerApp.Models.DTOs;

namespace ServerApp.Infrastructure;

public class GlobalExceptionFilter : IExceptionFilter
{
    public void OnException(ExceptionContext context)
    {
        // Логика определения кодов
        var (statusCode, customSubCode) = context.Exception switch
        {
            KeyNotFoundException => (StatusCodes.Status404NotFound, 01),
            ArgumentException => (StatusCodes.Status400BadRequest, 01),

            _ => (StatusCodes.Status500InternalServerError, 01)
        };
        
        var finalErrorCode = statusCode * 100 + customSubCode;
        var response = new ErrorResponse(context.Exception.Message, finalErrorCode);

        context.Result = new ObjectResult(response)
        {
            StatusCode = statusCode
        };

        context.ExceptionHandled = true;
    }
}