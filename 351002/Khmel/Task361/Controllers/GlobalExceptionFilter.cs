using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

public class GlobalExceptionFilter : IExceptionFilter
{
    public void OnException(ExceptionContext context)
    {
        // Don't handle if already handled or if result is already set
        if (context.ExceptionHandled)
            return;

        var (statusCode, errorCode, message) = context.Exception switch
        {
            KeyNotFoundException ex => (404, 40401, ex.Message),
            ArgumentException ex => (400, 40001, ex.Message),
            UnauthorizedAccessException => (401, 40100, "Unauthorized"),
            _ => (500, 50001, "Internal server error")
        };

        context.Result = new ObjectResult(new ErrorResponse
        {
            ErrorMessage = message,
            ErrorCode = errorCode
        })
        {
            StatusCode = statusCode
        };

        context.ExceptionHandled = true;
    }
}