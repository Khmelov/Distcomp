using System.Net;
using Domain.Exceptions;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;

namespace WebApi.Controllers;

public class ErrorController : ControllerBase
{
    [Route("/errors")]
    [ApiExplorerSettings(IgnoreApi = true)]
    public async Task<IActionResult> Errors()
    {
        var exceptionHandler = HttpContext.Features.Get<IExceptionHandlerFeature>();
        var exception = exceptionHandler.Error;
        var problem = ProblemDetailsFactory.CreateProblemDetails(HttpContext);
        problem.Instance = exceptionHandler.Path;
        problem.Detail = exception.Message;
        switch (exception)
        {
            case BadRequestException badRequestException:
                foreach (var validationError in badRequestException.Errors)
                {
                    problem.Extensions.Add(validationError.Key,validationError.Value);
                }
                problem.Status = (int)HttpStatusCode.BadRequest;
                break;
            case NotFoundException notFoundException:
                problem.Status = (int)HttpStatusCode.NotFound;
                break;
            default:
                problem.Status = (int)HttpStatusCode.InternalServerError;
                break;
        }

        var result = ProblemDetailsFactory.CreateProblemDetails(
            HttpContext,
            problem.Status);
        result.Instance = problem.Instance;
        result.Detail = problem.Detail;
        result.Extensions = problem.Extensions;
        return new ObjectResult(result);
    }
}