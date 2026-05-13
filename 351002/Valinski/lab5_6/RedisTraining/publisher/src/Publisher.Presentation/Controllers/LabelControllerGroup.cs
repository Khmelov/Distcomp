using AutoMapper;
using FluentValidation;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Features.Commands.CreateLabel;
using Publisher.Application.Features.Commands.DeleteLabel;
using Publisher.Application.Features.Commands.UpdateLabel;
using Publisher.Application.Features.Queries.GetAllLabels;
using Publisher.Application.Features.Queries.GetLabelById;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Extensions;

namespace Publisher.Presentation.Controllers;

public static class LabelControllerGroup
{
    public static RouteGroupBuilder MapLabelControllerGroup(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IMediator mediator, IMapper mapper) =>
        {
            var result = await mediator.Send(new GetAllLabelsQuery());
            return Results.Ok(mapper.Map<List<LabelResponse>>(result.Value));
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator, IMapper mapper) =>
        {
            var res = await mediator.Send(new GetLabelByIdQuery { Id = id });
            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.Ok(mapper.Map<LabelResponse>(res.Value));
        }).WithName("GetLabelById");

        group.MapPost("", async ([FromBody] LabelCreateRequest request, IMediator mediator, IMapper mapper, IValidator<LabelCreateRequest> validator) =>
        {
            var validationResult = await validator.ValidateAsync(request);
            if (!validationResult.IsValid) return Results.BadRequest(validationResult.Errors);

            var command = new CreateLabelCommand
            {
                Name = request.Name
            };

            var result = await mediator.Send(command);
            if (!result.IsSuccess) return result.ToProblemDetails();

            return Results.CreatedAtRoute("GetLabelById", new { id = result.Value!.Id }, mapper.Map<LabelResponse>(result.Value));
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var res = await mediator.Send(new DeleteLabelCommand { Id = id });
            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.NoContent();
        });

        group.MapPut("", async (LabelUpdateRequest request, IMediator mediator, IMapper mapper) =>
        {
            var res = await mediator.Send(new UpdateLabelCommand
            {
                Id = request.Id,
                Name = request.Name
            });

            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.Ok(mapper.Map<LabelResponse>(res.Value));
        });

        return group;
    }
}
