using System.Text.Json;
using AutoMapper;
using Discussion.Application.Features.Commands;
using Discussion.Application.Features.Queries;
using MassTransit;
using MediatR;
using Shared.Messages;

namespace Discussion.Infrastructure.Broker.Consumers;

public class ReactionConsumer : IConsumer<ReactionMessage>
{
    private readonly IMediator _mediator;
    private readonly IMapper _mapper;
    private readonly ITopicProducer<ReactionMessage> _topicProducer;

    public ReactionConsumer(IMediator mediator, ITopicProducer<ReactionMessage> topicProducer, IMapper mapper)
    {
        _mediator = mediator;
        _topicProducer = topicProducer;
        _mapper = mapper;
    }

    public async Task Consume(ConsumeContext<ReactionMessage> context)
    {
        var value = context.Message;

        switch (value.ActionType)
        {
            case ReactionMessages.Create:
                await HandleCreate(value.Entity);
                break;

            case ReactionMessages.GetAll:
                await HandleGetAll(context);
                break;

            case ReactionMessages.GetById:
                await HandleGetById(context);
                break;

            case ReactionMessages.Update:
                await HandleUpdate(context);
                break;
            case ReactionMessages.Delete:
                await HandleDelete(context);
                break;

            default:
                // log unknown action type
                break;
        }
    }

    private async Task HandleDelete(ConsumeContext<ReactionMessage> context)
    {
        var msg = context.Message;
        
        var idToDelete = msg.Entity!.Value.Deserialize<long>();
        var command = new DeleteReactionCommand() { Id = idToDelete };
        await _mediator.Send(command);
        var responseMsg = new ReactionMessage()
        {
            ActionType = ReactionMessages.Delete,
            Entity = JsonSerializer.SerializeToElement(idToDelete),
            CorrelationId = context.Message.CorrelationId
        };
        await _topicProducer.Produce(responseMsg);
    }

    private async Task HandleCreate(JsonElement? entity)
    {
        if (entity is null)
        {
            return;
        }

        var req = entity.Value.Deserialize<CreateReactionRequestMsg>();
        if (req is null)
        {
            return;
        }

        var command = new AddReactionCommand
        {
            Id = req.Id,
            TopicId = req.TopicId,
            Content = req.Content,
            Country = req.Country
        };

        await _mediator.Send(command);
    }

    private async Task HandleGetAll(ConsumeContext<ReactionMessage> context)
    {
        var query = new GetAllQuery();
        var result = await _mediator.Send(query);

        if (!result.IsSuccess)
        {
            return;
        }

        var reactions = _mapper.Map<List<ReactionResponseMsg>>(result.Value!);
        var msg = new ReactionMessage()
        {
            Entity = JsonSerializer.SerializeToElement(reactions),
            ActionType = ReactionMessages.GetAll,
            CorrelationId = context.Message.CorrelationId
        };

        await _topicProducer.Produce(msg);
    }

    private async Task HandleGetById(ConsumeContext<ReactionMessage> context)
    {
        var id = context.Message.Entity!.Value.Deserialize<long>();
        var query = new GetByIdQuery() { Id = id };
        var result = await _mediator.Send(query);
        if (!result.IsSuccess)
        {
            return;
        }

        var msg = new ReactionMessage()
        {
            Entity = JsonSerializer.SerializeToElement(result.Value),
            ActionType = ReactionMessages.GetById,
            CorrelationId = context.Message.CorrelationId
        };

        await _topicProducer.Produce(msg);
    }

    private async Task HandleUpdate(ConsumeContext<ReactionMessage> context)
    {
        var msg = context.Message.Entity!.Value.Deserialize<UpdateReactionRequestMsg>();

        var id = msg.Id;
        var updateRequest = new UpdateReactionCommand()
        {
            Id = id,
            TopicId = msg.TopicId,
            Content = msg.Content,
            Country = msg.Country
        };

        var result = await _mediator.Send(updateRequest);

        var reactionMsg = result.IsSuccess && result.Value is not null
            ? new ReactionResponseMsg
            {
                Id = result.Value.Id,
                TopicId = result.Value.TopicId,
                Content = result.Value.Content,
                Country = result.Value.Country,
                Status = ReactionStatuses.Active
            }
            : new ReactionResponseMsg
            {
                Id = 0,
                TopicId = 0,
                Content = string.Empty,
                Country = string.Empty,
                Status = ReactionStatuses.Declined
            };

        var responseMsg = new ReactionMessage()
        {
            ActionType = ReactionMessages.Update,
            Entity = JsonSerializer.SerializeToElement(reactionMsg),
            CorrelationId = context.Message.CorrelationId
        };

        await _topicProducer.Produce(responseMsg);
    }
}
