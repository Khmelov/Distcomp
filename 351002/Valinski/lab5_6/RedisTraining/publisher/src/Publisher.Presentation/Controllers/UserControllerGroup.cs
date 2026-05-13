using AutoMapper;
using FluentValidation;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Features.Commands;
using Publisher.Application.Features.Commands.CreateUser;
using Publisher.Application.Features.Commands.DeleteUser;
using Publisher.Application.Features.Commands.UpdateUser;
using Publisher.Application.Features.Queries.GetAllUsers;
using Publisher.Application.Features.Queries.GetUserById;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Extensions;

namespace Publisher.Presentation.Controllers;

public static class UserControllerGroup
{
    public static RouteGroupBuilder MapUserControllerGroup(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IMediator mediator, IMapper mapper) =>
        {
            var query = new GetAllUsersQuery();
            var result = await mediator.Send(query);

            return Results.Ok(mapper.Map<List<UserResponse>>(result.Value));
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator, IMapper mapper) =>
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

                return Results.Ok(mapper.Map<UserResponse>(res.Value));
            })
            .WithName("GetUserById");

        group.MapPost("",
            async ([FromBody] UserCreateRequest request, IMediator mediator, IMapper mapper,
                IValidator<UserCreateRequest> validator) =>
            {
                var validationResult = await validator.ValidateAsync(request);
                if (!validationResult.IsValid)
                {
                    return Results.BadRequest(validationResult.Errors);
                }

                var command = new CreateUserCommand()
                {
                    Login = request.Login,
                    Firstname = request.Firstname,
                    Lastname = request.Lastname,
                    Password = request.Password
                };

                var result = await mediator.Send(command);
                if (!result.IsSuccess)
                {
                    return result.ToProblemDetails();
                }

                return Results.CreatedAtRoute("GetUserById", new { id = result.Value!.Id },
                    mapper.Map<UserResponse>(result.Value));
            });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var deleteUserCommand = new DeleteUserCommand()
            {
                Id = id
            };

            var res = await mediator.Send(deleteUserCommand);
            if (!res.IsSuccess)
            {
                return res.ToProblemDetails();
            }

            return Results.NoContent();
        });

        group.MapPut("", async (UserUpdateRequest request, IMediator mediator, IMapper mapper) =>
        {
            var updateUserCommand = new UpdateUserCommand()
            {
                Id = request.Id,
                Firstname = request.Firstname,
                Lastname = request.Lastname,
                Login = request.Login,
                Password = request.Password
            };

            var res = await mediator.Send(updateUserCommand);
            if (!res.IsSuccess)
            {
                return res.ToProblemDetails();
            }

            return Results.Ok(res.Value);
        });


        return group;
    }
}
