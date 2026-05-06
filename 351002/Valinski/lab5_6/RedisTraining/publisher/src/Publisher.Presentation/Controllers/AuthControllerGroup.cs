using System.Security.Claims;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Features.Commands.CreateAccount;
using Publisher.Application.Features.Commands.DeleteUser;
using Publisher.Application.Features.Commands.LoginAccount;
using Publisher.Application.Features.Queries.GetAllUsers;
using Publisher.Application.Features.Queries.GetUserById;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Extensions;

namespace Publisher.Presentation.Controllers;

public static class AuthControllerGroup
{
    public static RouteGroupBuilder MapAuthControllerGroup(this RouteGroupBuilder group)
    {
        group.MapPost("", async (UserCreateRequest request, IMediator mediator) =>
        {
            var command = new CreateAccountCommand()
            {
                Login = request.Login,
                Lastname = request.Lastname,
                Firstname = request.Firstname,
                Password = request.Password,
                Role = request.Role
            };

            var result = await mediator.Send(command);

            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.CreatedAtRoute("GetUserById", new { id = result.Value!.Id }, result.Value);
        });

        group.MapGet("", async (IMediator mediator) =>
            {
                var query = new GetAllUsersQuery();

                var res = await mediator.Send(query);
                if (!res.IsSuccess)
                {
                    return res.ToProblemDetails();
                }

                return Results.Ok(res.Value);
            })
            .RequireAuthorization();

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var query = new GetUserByIdQuery()
            {
                Id = id,
            };

            var res = await mediator.Send(query);
            if (!res.IsSuccess)
            {
                return res.ToProblemDetails();
            }

            return Results.Ok(res.Value);
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IMediator mediator, ClaimsPrincipal user) =>
        {
            var currentUserId = user.FindFirstValue(ClaimTypes.NameIdentifier);
            if (!user.IsInRole("Admin") && id.ToString() != currentUserId)
            {
                return Results.Forbid();
            }

            var query = new DeleteUserCommand()
            {
                Id = id,
            };

            var res = await mediator.Send(query);
            if (!res.IsSuccess)
            {
                return res.ToProblemDetails();
            }

            return Results.NoContent();
        }).RequireAuthorization();

        return group;
    }

    public static RouteGroupBuilder MapLoginControllerGroup(this RouteGroupBuilder group)
    {
        group.MapPost("", async (LoginAccountRequest request, IMediator mediator) =>
        {
            var command = new LoginAccountCommand()
            {
                Login = request.Login,
                Password = request.Password,
            };

            var result = await mediator.Send(command);

            if (!result.IsSuccess)
            {
                return result.ToProblemDetails();
            }

            return Results.Ok(new { Access_token = result.Value });
        });

        return group;
    }
}
