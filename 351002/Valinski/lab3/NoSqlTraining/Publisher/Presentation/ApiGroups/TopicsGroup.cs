using AutoMapper;
using Domain.Models;
using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Presentation.Contracts;

namespace Presentation.ApiGroups;

public static class TopicsGroup
{
    public static RouteGroupBuilder MapTopics(this RouteGroupBuilder group)
    {
        group.MapPost("", async (TopicCreateRequest request, IMapper mapper, PublisherDbContext context) =>
        {
            var topic = mapper.Map<Topic>(request);
            
            await context.Topics.AddAsync(topic);
            await context.SaveChangesAsync();

            return Results.CreatedAtRoute("GetTopic", new { id = topic.Id }, topic);
        });

        group.MapGet("", async (PublisherDbContext context) =>
        {
            return Results.Ok(await context.Topics.ToListAsync());
        });

        group.MapGet("/{id:long}", async (long id, PublisherDbContext context) =>
        {
            var topic = await context.Topics.FindAsync(id);
            return topic is null ? Results.NotFound() : Results.Ok(topic);
        }).WithName("GetTopic");

        group.MapPut("", async (TopicUpdateRequest request, IMapper mapper, PublisherDbContext context) =>
        {
            var topic = await context.Topics.FindAsync(request.Id);
            if (topic is null)
            {
                return Results.NotFound();
            }

            mapper.Map(request, topic);
            await context.SaveChangesAsync();

            return Results.Ok(topic);
        });

        group.MapDelete("/{id:long}", async (long id, PublisherDbContext context) =>
        {
            var topic = await context.Topics.FindAsync(id);
            if (topic is null)
            {
                return Results.NotFound();
            }

            context.Topics.Remove(topic);
            await context.SaveChangesAsync();

            return Results.NoContent();
        });

        return group;
    }
}
