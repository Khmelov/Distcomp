using Application.Features.Marker.Commands;
using Application.Features.Marker.Queries;
using Asp.Versioning;
using Domain.Shared;
using MediatR;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using SocialNet.Abstractions;

namespace SocialNet.Controllers;

[Route("markers")]
[ApiVersion("1.0")]
public class MarkerController : MediatrController
{
    private readonly ILogger<MarkerController> _logger;

    public MarkerController(IMediator mediator, ILogger<MarkerController> logger) : base(mediator)
    {
        _logger = logger;
    }

    [HttpPost]
    public async Task<IActionResult> CreateMarker(CreateMarkerCommand command){
        var result = await _mediator.Send(command);
        
        if (!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status201Created, result.Value);
    }

    [HttpGet]
    public async Task<IActionResult> GetMarkers()
    {
        var result = await _mediator.Send(new GetMarkersQuery());

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetMarkerById(long id)
    {
        var result = await _mediator.Send(new GetMarkerByIdQuery(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateMarker(UpdateMarkerCommand command){
        var result = await _mediator.Send(command);

        if(!result.IsSuccess){
            return HandleFailure(result);
        }
        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteMarker(long id){
        var result = await _mediator.Send(new DeleteMarkerCommand(id));

        if (!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status204NoContent); 
    }
}
