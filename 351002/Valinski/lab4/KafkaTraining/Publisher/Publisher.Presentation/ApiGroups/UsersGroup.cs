using AutoMapper;
using FluentValidation;
using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Publisher.Domain.Models;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.ApiGroups;

public static class UsersGroup 
{
    public static RouteGroupBuilder MapUsers(this RouteGroupBuilder group)
    {
        group.MapPost("", async (UserCreateRequest request, IMapper mapper, IValidator<UserCreateRequest> validator, PublisherDbContext context) =>
        {
            var validationResult = await validator.ValidateAsync(request);

            if (!validationResult.IsValid)
            {
                return Results.BadRequest(validationResult.Errors);
            }
            
            User user = mapper.Map<UserCreateRequest, User>(request);
            var userFromDb = await context.Users.FirstOrDefaultAsync(x => x.Login == user.Login);
            if (userFromDb is not null)
            {
                return Results.StatusCode(403);
            }
            await context.Users.AddAsync(user);
            await context.SaveChangesAsync();
            
            return Results.CreatedAtRoute("GetUser", new { id = user.Id }, user);
        });

        group.MapGet("", async (PublisherDbContext context) => 
            Results.Ok(await context.Users.ToListAsync()));

        group.MapGet("{id:long}", async (long id, PublisherDbContext context) =>
        {
            var user = await context.Users.FindAsync(id);
            return user is null ? Results.NotFound() : Results.Ok(user);
        }).WithName("GetUser");

        group.MapPut("", async (UserUpdateRequest request, IMapper mapper, PublisherDbContext context) =>
        {
            var user = await context.Users.FindAsync(request.Id);
            if (user is null)
            {
                return Results.NotFound();
            }
            
            mapper.Map(request, user);
            await context.SaveChangesAsync();
            
            return Results.Ok(user);
        });

        group.MapDelete("/{id:long}", async (long id, PublisherDbContext context) =>
        {
            var user = await context.Users.FindAsync(id);
            if (user is null)
            {
                return Results.NotFound();
            }
            
            context.Users.Remove(user);
            await context.SaveChangesAsync();
            
            return Results.NoContent();
        });
        
        return group;
    }
}
