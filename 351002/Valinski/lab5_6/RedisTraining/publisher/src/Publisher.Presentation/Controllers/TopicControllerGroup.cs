using AutoMapper;
using FluentValidation;
using MediatR;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Features.Commands.CreateTopic;
using Publisher.Application.Features.Commands.DeleteTopic;
using Publisher.Application.Features.Commands.UpdateTopic;
using Publisher.Application.Features.Queries.GetAllTopics;
using Publisher.Application.Features.Queries.GetTopicById;
using Publisher.Presentation.Contracts;
using Publisher.Presentation.Extensions;

namespace Publisher.Presentation.Controllers;

public static class TopicControllerGroup
{
    public static RouteGroupBuilder MapTopicControllerGroup(this RouteGroupBuilder group)
    {
        group.MapGet("", async (IMediator mediator, IMapper mapper) =>
        {
            var result = await mediator.Send(new GetAllTopicsQuery());
            return Results.Ok(mapper.Map<List<TopicResponse>>(result.Value));
        });

        group.MapGet("{id:long}", async ([FromRoute] long id, IMediator mediator, IMapper mapper) =>
        {
            var res = await mediator.Send(new GetTopicByIdQuery { Id = id });
            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.Ok(mapper.Map<TopicResponse>(res.Value));
        }).WithName("GetTopicById");

        group.MapPost("", async ([FromBody] TopicCreateRequest request, IMediator mediator, IMapper mapper, IValidator<TopicCreateRequest> validator) =>
        {
            var validationResult = await validator.ValidateAsync(request);
            if (!validationResult.IsValid) return Results.BadRequest(validationResult.Errors);

            var command = new CreateTopicCommand
            {
                UserId = request.UserId,
                Title = request.Title,
                Content = request.Content
            };

            var result = await mediator.Send(command);
            if (!result.IsSuccess) return result.ToProblemDetails();

            return Results.CreatedAtRoute("GetTopicById", new { id = result.Value!.Id }, mapper.Map<TopicResponse>(result.Value));
        });

        group.MapDelete("{id:long}", async ([FromRoute] long id, IMediator mediator) =>
        {
            var res = await mediator.Send(new DeleteTopicCommand { Id = id });
            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.NoContent();
        });

        group.MapPut("", async (TopicUpdateRequest request, IMediator mediator, IMapper mapper) =>
        {
            var res = await mediator.Send(new UpdateTopicCommand
            {
                Id = request.Id,
                Title = request.Title,
                Content = request.Content
            });

            if (!res.IsSuccess) return res.ToProblemDetails();
            return Results.Ok(mapper.Map<TopicResponse>(res.Value));
        });

        return group;
    }
}
