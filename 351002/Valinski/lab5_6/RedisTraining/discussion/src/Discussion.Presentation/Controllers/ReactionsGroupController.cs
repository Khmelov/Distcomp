using AutoMapper;
using Discussion.Application.Features.Commands;
using Discussion.Application.Features.Queries;
using Discussion.Presentation.Contracts;
using Discussion.Presentation.Extensions;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Presentation.Controllers;

public static class ReactionsGroupController
{
    public static RouteGroupBuilder MapReactionGroup(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IMapper mapper, IMediator mediator) =>
        {
            var query = new GetAllQuery();
            var queryResult = await mediator.Send(query);
            if (!queryResult.IsSuccess)
            {
                return queryResult.ToProblemDetails();
            }

            var result = mapper.Map<List<ReactionResponse>>(queryResult.Value);
            return Results.Ok(result);
        });

        group.MapPost("", async (ReactionRequest request, IMapper mapper, IMediator mediator) =>
        {
            var command = new AddReactionCommand()
            {
                Id = request.Id,
                Content = request.Content,
                Country = request.Country,
                TopicId = request.TopicId,
            };

            var commandResult = await mediator.Send(command);
            if (!commandResult.IsSuccess)
            {
                return commandResult.ToProblemDetails();
            }

            return Results.CreatedAtRoute("GetReactionById", new { id = commandResult.Value!.Id }, commandResult.Value);
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator, IMapper mapper) =>
        {
            var query = new GetByIdQuery()
            {
                Id = id
            };

            var queryResult = await mediator.Send(query);
            if (!queryResult.IsSuccess)
            {
                return queryResult.ToProblemDetails();
            }

            var result = mapper.Map<ReactionResponse>(queryResult.Value);
            return Results.Ok(result);
        }).WithName("GetReactionById");
        
        group.MapPut("", async ([FromBody] ReactionUpdateRequest request, IMediator mediator) =>
        {
            var command = new UpdateReactionCommand()
            {
                Id = request.Id,
                Content = request.Content,
                Country = request.Country,
                TopicId = request.TopicId,
            };

            var commandResult = await mediator.Send(command);
            if (!commandResult.IsSuccess)
            {
                return commandResult.ToProblemDetails();
            }

            return Results.Ok(commandResult.Value);
        });

        return group;
    }
}
