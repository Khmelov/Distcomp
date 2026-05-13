using MediatR;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Features.Commands.CreateReaction;
using Publisher.Application.Features.Commands.DeleteReaction;
using Publisher.Application.Features.Commands.UpdateReaction;
using Publisher.Application.Features.Queries.GetAllReactions;
using Publisher.Application.Features.Queries.GetReactionById;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Extensions;

namespace Publisher.Presentation.Controllers;

public static class ReactionControllerGroup
{
    public static RouteGroupBuilder MapReactionControllerGroup(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IMediator mediator) =>
        {
            var query = new GetAllReactionsQuery();

            var result = await mediator.Send(query);
            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.Ok(result.Value);
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var query = new GetReactionByIdQuery()
            {
                Id = id
            };

            var result = await mediator.Send(query);
            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.Ok(result.Value);
        }).WithName("GetReactionById");

        group.MapPost("", async (ReactionRequest request, IMediator mediator) =>
        {
            var command = new CreateReactionCommand()
            {
                TopicId = request.TopicId,
                Content = request.Content,
                Country = request.Country,
            };

            var result = await mediator.Send(command);

            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.CreatedAtRoute("GetReactionById", new { id = result.Value!.Id }, result.Value);
        });

        group.MapPut("", async (ReactionUpdateRequest request, IMediator mediator) =>
        {   
            var command = new UpdateReactionCommand()
            {
                Id = request.Id,
                TopicId = request.TopicId,
                Content = request.Content,
                Country = request.Country,
            };

            var result = await mediator.Send(command);
            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.Ok(result.Value);
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var command = new DeleteReactionCommand()
            {
                Id = id,
            };
            
            var result = await mediator.Send(command);
            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }
            
            return Results.NoContent();
        });
        
        return group;
    }
}
