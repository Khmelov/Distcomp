using System.Text.Json;
using Additions.Service;
using Additions.Service.EventService.Interfaces;
using CommentMicroservice.Service.DTOs;
using CommentMicroservice.Service.Interfaces;
using CommonAPI.Service.Events;

namespace CommentMicroservice.Service.Implementations.EventHandlers;

public class GetManyCommentsHandler : IEventHandler
{
    private readonly ICommentService commentService;
    private readonly IEventProducerService producerService;
    private readonly string eventTopic;

    public string SupportedOperation
    {
        get
        {
            return EventNames.MANY_COMMENTS_GET;
        }
    }

    public GetManyCommentsHandler(ICommentService commentService, IEventProducerService producerService,
                                    IConfiguration configuration)
    {
        this.commentService = commentService;
        this.producerService = producerService;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }

    public async Task HandleMessage(EventMessage message)
    {
        EventMessage response = null!;
        string? error = null;
        try
        {
            var comments = await commentService.GetAllCommentsAsync();
            ManyCommentsPayload formattedComments = new([..comments.Select(com =>
            {
                return new CommentPayload()
                {
                    Id = com.Id,
                    ArticleId = com.ArticleId,
                    Content = com.Content
                };
            })]);
            response = new()
            {
                Operation = EventNames.OPERATION_END,
                Payload = JsonSerializer.Serialize(formattedComments),
                InReplyTo = message.MessageId
            };
        }
        catch (ServiceException e)
        {
            error = e.Message;
        }
        if (error != null)
        {
            response = new()
            {
                Operation = EventNames.OPERATION_END,
                Error = error,
                InReplyTo = message.MessageId
            };
        }
        await producerService.ProduceEventAsync(eventTopic, response);
    }
}