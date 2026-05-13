using discussion.Models.DTO.Responses;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace discussion.Errors;

public class GlobalExceptionFilter : IExceptionFilter
{
    public void OnException(ExceptionContext context)
    {
        var ex = context.Exception;

        if (ex is KeyNotFoundException)
        {
            context.Result = new NotFoundObjectResult(
                new ErrorResponse { ErrorCode = 40401, ErrorMessage = ex.Message });
        }
        else if (ex is ArgumentException)
        {
            context.Result = new BadRequestObjectResult(
                new ErrorResponse { ErrorCode = 40001, ErrorMessage = ex.Message });
        }
        else
        {
            context.Result = new ObjectResult(
                new ErrorResponse { ErrorCode = 50001, ErrorMessage = "Internal server error" })
            {
                StatusCode = 500
            };
        }

        context.ExceptionHandled = true;
    }
}
