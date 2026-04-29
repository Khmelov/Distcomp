using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

public class GlobalExceptionFilter : IExceptionFilter
{
    public void OnException(ExceptionContext context)
    {
        var (statusCode, errorCode, message) = context.Exception switch
        {
            KeyNotFoundException ex => (404, 40401, ex.Message),
            ArgumentException ex => (400, 40001, ex.Message),
            _ => (500, 50001, "ошибка сервера")
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