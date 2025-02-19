using System;
using System.Reflection.Metadata;
using Application.Features.Reaction.Commands;
using Application.Features.Reaction.Queries;
using Asp.Versioning;
using Domain.Shared;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using SocialNet.Abstractions;

namespace SocialNet.Controllers;

[Route("reactions")]
[ApiVersion("1.0")]
public class ReactionController : MediatrController
{
    public ReactionController(IMediator mediator) : base(mediator)
    {
    }

    [HttpPost]
    public async Task<IActionResult> CreateReaction(CreateReactionCommand command){
        var result = await _mediator.Send(command);

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status201Created, result.Value);
    }

    [HttpGet]
    public async Task<IActionResult> GetReactions(){
        var result = await _mediator.Send(new GetReactionsQuery());

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }


    [HttpGet("{id}")]
    public async Task<IActionResult> GetReactionByIdQuery(long id){
        var result = await _mediator.Send(new GetReactionByIdQuery(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateReaction(UpdateReactionCommand command){
        var result = await _mediator.Send(command);

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }


    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteReaction(long id){
        var result = await _mediator.Send(new DeleteReactionCommand(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status204NoContent);
    }
}
