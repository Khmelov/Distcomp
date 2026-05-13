using lab1.Models.DTO.Responses;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.EntityFrameworkCore;

namespace lab1.Errors;

public class GlobalExceptionFilter : IExceptionFilter
{
    public void OnException(ExceptionContext context)
    {
        var ex = context.Exception;

        if (ex is KeyNotFoundException)
        {
            context.Result = new NotFoundObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 40401,
                    ErrorMessage = ex.Message
                });
        }
        else if (ex is ArgumentException)
        {
            context.Result = new BadRequestObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 40001,
                    ErrorMessage = ex.Message
                });
        }
        else if (ex is EditorLoginAlreadyExistsException)
        {
            context.Result = new ObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 40301,
                    ErrorMessage = ex.Message
                })
            {
                StatusCode = 403
            };
        }
        else if (ex is IssueTitleAlreadyExistsException)
        {
            context.Result = new ObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 40302,
                    ErrorMessage = ex.Message
                })
            {
                StatusCode = 403
            };
        }
        else if (ex is DbUpdateException)
        {
            context.Result = new ConflictObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 40901,
                    ErrorMessage = "Operation conflicts with existing related data in the database"
                });
        }
        else if (ex is TimeoutException)
        {
            context.Result = new ObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 50401,
                    ErrorMessage = ex.Message
                })
            {
                StatusCode = 504
            };
        }
        else
        {
            context.Result = new ObjectResult(
                new ErrorResponse
                {
                    ErrorCode = 50001,
                    ErrorMessage = "Internal server error"
                })
            {
                StatusCode = 500
            };
        }

        context.ExceptionHandled = true;
    }
}
