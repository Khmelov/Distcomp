using Discussion.Domain.Abstractions;
using Discussion.Domain.Models;
using Discussion.Presentation.Contracts;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Presentation.MinimalApi;

public static class ReactionGroup
{
    public static RouteGroupBuilder MapReactions(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IReactionRepository repository) =>
        {
            return Results.Ok(await repository.GetAllReactions());
        });
        
        group.MapPost("", async ([FromBody] CreateReactionRequest request, IReactionRepository repository) =>
        {
            Reaction reaction = new()
            {
                Country = request.Country,
                TopicId = request.TopicId,
                Content = request.Content
            };

            var res = await repository.CreateReaction(reaction);

            return Results.CreatedAtRoute("GetById", new { id = res.Id }, res);
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IReactionRepository repository) =>
        {
            var reactionFromRepo = await repository.GetById(id);

            if (reactionFromRepo == null)
            {
                return Results.BadRequest(new {Error = "NotFound"});
            }
            
            return Results.Ok(reactionFromRepo);
        }).WithName("GetById");

        group.MapPut("", async ([FromBody] UpdateReactionRequest request, IReactionRepository repository) =>
        {
            Reaction reaction = new()
            {
                Id =  request.Id,
                Country = request.Country,
                TopicId = request.TopicId,
                Content = request.Content
            };

            var res = await repository.UpdateReaction(reaction);
            return res;
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IReactionRepository repository) =>
        {
            // var reaction = await repository.GetById(id);
            // if (reaction is null) return Results.NotFound();
            
            await repository.DeleteReaction(id);
            return Results.NoContent();
        });
        
        return group;
    }
}
