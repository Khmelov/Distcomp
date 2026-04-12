using Microsoft.AspNetCore.Mvc;
using Presentation.Clients;
using Presentation.Contracts;

namespace Presentation.ApiGroups;

public static class ReactionsGroup
{
    public static RouteGroupBuilder MapReactions(this RouteGroupBuilder group)
    {
        group.MapGet("", async (ReactionsServiceClient client) =>
        {
            return await client.GetAllReactions();
        });

        group.MapPost("", async ([FromBody] CreateReactionRequest request, ReactionsServiceClient client) =>
        {
            var createdReaction = await client.CreateReaction(request);
        
            return Results.CreatedAtRoute("GetReactionById", new { id = createdReaction.Id }, createdReaction);
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, ReactionsServiceClient client) =>
        {
            return await client.GetReactionById(id);
        }).WithName("GetReactionById");

        group.MapPut("", async ([FromBody] ReactionUpdateRequest request, ReactionsServiceClient client) =>
        {
            return await client.UpdateReaction(request); 
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, ReactionsServiceClient client) =>
        {
            await client.DeleteReaction(id); 
            return Results.NoContent();
        });
        
        return group;
    }
}
