using Microsoft.AspNetCore.Mvc;
using Publisher.Presentation.Clients;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.ApiGroups;

public static class ReactionsGroup
{
    public static RouteGroupBuilder MapReactions(this RouteGroupBuilder group)
    {
        group.MapGet("", async (ReactionsServiceClient client) => { return await client.GetAllReactions(); });
        
        group.MapPost("", async ([FromBody] CreateReactionRequest request, ReactionsServiceClient client) =>
        {
            var id = await client.CreateReaction(request);
            
            return Results.CreatedAtRoute("GetReactionById", new { id = id }, new {topicId = request.TopicId, id = id, content = request.Content});
        });

        group.MapGet("{id:long}",
            async ([FromRoute] long id, ReactionsServiceClient client) =>
            {
                var res = await client.GetReactionById(id);
                if (res == null)
                {
                    return Results.NotFound();
                }
                
                return Results.Ok(res);
            }).WithName("GetReactionById");

        group.MapDelete("{id:long}", async ([FromRoute] long id, ReactionsServiceClient client) =>
        {
            var res = await client.DeleteReaction(id);
            if (res)
            {
                return Results.NoContent();
            }

            return Results.BadRequest();
        });

        group.MapPut("", async (ReactionUpdateRequest request, ReactionsServiceClient client) =>
        {
            var res = await client.UpdateReaction(request);
            return Results.Ok(res);
        });
        
        return group;
    }
}
