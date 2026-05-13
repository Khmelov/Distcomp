using Shared.Commons;

namespace Publisher.Presentation.Extensions;

public static class ResultExtensions
{
    public static IResult ToProblemDetails(this Result result)
    {
        if (result.IsSuccess)
        {
            throw new InvalidOperationException("Cannot convert success result to problem details");
        }

        return CreateProblemDetails(result.ErrorType, result.ErrorMessage);
    }

    public static IResult ToProblemDetails<T>(this Result<T> result)
    {
        if (result.IsSuccess)
        {
            throw new InvalidOperationException("Cannot convert success result to problem details");
        }

        return CreateProblemDetails(result.ErrorType, result.ErrorMessage);
    }

    private static IResult CreateProblemDetails(ErrorType errorType, string? message) =>
        errorType switch
        {
            ErrorType.Validation => Results.Problem(
                statusCode: StatusCodes.Status400BadRequest,
                title: "Validation Error",
                detail: message),

            ErrorType.NotFound => Results.Problem(
                statusCode: StatusCodes.Status404NotFound,
                title: "Not Found",
                detail: message),

            ErrorType.Conflict => Results.Problem(
                statusCode: StatusCodes.Status403Forbidden,
                title: "Conflict",
                detail: message),

            ErrorType.Unauthorized => Results.Unauthorized(),

            _ => Results.Problem(
                statusCode: StatusCodes.Status500InternalServerError,
                title: "Server Error",
                detail: message)
        };
}
