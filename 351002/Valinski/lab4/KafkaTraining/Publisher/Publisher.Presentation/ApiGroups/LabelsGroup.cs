using AutoMapper;
using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Publisher.Domain.Models;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.ApiGroups;

public static class LabelsGroup
{
    public static RouteGroupBuilder MapLabels(this RouteGroupBuilder group)
    {
        group.MapPost("", async (LabelCreateRequest request, IMapper mapper, PublisherDbContext context) =>
        {
            var label = mapper.Map<Label>(request);
            
            await context.Labels.AddAsync(label);
            await context.SaveChangesAsync();

            return Results.CreatedAtRoute("GetLabel", new { id = label.Id }, label);
        });

        group.MapGet("", async (PublisherDbContext context) =>
        {
            return Results.Ok(await context.Labels.ToListAsync());
        });

        group.MapGet("/{id:long}", async (long id, PublisherDbContext context) =>
        {
            var label = await context.Labels.FindAsync(id);
            return label is null ? Results.NotFound() : Results.Ok(label);
        }).WithName("GetLabel");

        group.MapPut("", async (LabelUpdateRequest request, IMapper mapper, PublisherDbContext context) =>
        {
            var label = await context.Labels.FindAsync(request.Id);
            if (label is null)
            {
                return Results.NotFound();
            }

            mapper.Map(request, label);
            await context.SaveChangesAsync();

            return Results.Ok(label);
        });

        group.MapDelete("/{id:long}", async (long id, PublisherDbContext context) =>
        {
            var label = await context.Labels.FindAsync(id);
            if (label is null)
            {
                return Results.NotFound();
            }

            context.Labels.Remove(label);
            await context.SaveChangesAsync();

            return Results.NoContent();
        });

        return group;
    }
}
