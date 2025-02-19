using System;
using System.Net;
using Domain.Shared;
using Domain.Shared.Validation;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace SocialNet.Abstractions;

[ApiController]
public class MediatrController : ControllerBase
{
    protected readonly IMediator _mediator;
    protected MediatrController(IMediator mediator) => _mediator = mediator;

    protected IActionResult HandleFailure(Result result) =>
        result switch {
            { IsSuccess: true } => throw new InvalidOperationException(),
            IValidationResult validationResult =>
                StatusCode(
                    result.Error.HttpStatusCode,
                    CreateValidationProblemDetails(result.Error, validationResult.Errors)), 
            _ =>
                StatusCode(
                    result.Error.HttpStatusCode,
                    CreateErrorProblemDetails(result.Error)
                )
        };

    private static object CreateValidationProblemDetails(Error error, Error[]? errors) => new
    {
        errorMessage = error.Message,
        errorCode = error.Code,
        errorDetails = errors
    };

    private static object CreateErrorProblemDetails(Error error) => new
    {
        errorMessage = error.Message,
        errorCode = error.Code
    };
}