using System;
using Application.Features.Tweet.Commands;
using Application.Features.Tweet.Queries;
using Asp.Versioning;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using SocialNet.Abstractions;

namespace SocialNet.Controllers;

[Route("tweets")]
[ApiVersion("1.0")]
public class TweetController : MediatrController
{
    public TweetController(IMediator mediator) : base(mediator)
    {
    }

    [HttpPost]
    public async Task<IActionResult> CreateTweet(CreateTweetCommand command){
        var result = await _mediator.Send(command);
        
        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status201Created, result.Value);
    }

    [HttpGet]
    public async Task<IActionResult> GetTweets(){
        var result = await _mediator.Send(new GetTweetsQuery());

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetTweetById(long id){
        var result = await _mediator.Send(new GetTweetByIdQuery(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateTweet(UpdateTweetCommand command){
        var result = await _mediator.Send(command);

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status200OK, result.Value);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteTweet(long id){
        var result = await _mediator.Send(new DeleteTweetCommand(id));

        if(!result.IsSuccess){
            return HandleFailure(result);
        }

        return StatusCode(StatusCodes.Status204NoContent);
    }

}
