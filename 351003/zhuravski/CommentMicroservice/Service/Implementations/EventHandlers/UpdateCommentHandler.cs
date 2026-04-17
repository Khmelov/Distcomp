using System.Text.Json;
using Additions.Messaging;
using Additions.Messaging.Interfaces;
using Additions.Service;
using CommentMicroservice.Service.DTOs;
using CommentMicroservice.Service.Interfaces;
using CommonAPI.Messaging;

namespace CommentMicroservice.Service.Implementations.EventHandlers;

public class UpdateCommentHandler : IEventHandler
{
    private readonly ICommentService commentService;
    private readonly IEventProducer producerService;
    private readonly string eventTopic;

    public string SupportedOperation
    {
        get
        {
            return EventNames.COMMENT_UPDATE;
        }
    }

    public UpdateCommentHandler(ICommentService commentService, IEventProducer producerService,
                                    IConfiguration configuration)
    {
        this.commentService = commentService;
        this.producerService = producerService;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }

    public async Task HandleMessage(EventMessage message)
    {
        EventMessage response = null!;
        CommentPayload? payload = message.GetPayload<CommentPayload>();
        string? error = null;
        if (payload != null) {
            try
            {
                var comment = await commentService.UpdateCommentByIdAsync(payload.Id,
                    new CommentRequestDTO()
                    {
                        Id = payload.Id,
                        ArticleId = payload.ArticleId,
                        Content = payload.Content
                    }
                );
                CommentPayload formattedComment = new()
                {
                    Id = comment.Id,
                    ArticleId = comment.ArticleId,
                    Content = comment.Content
                };
                response = new()
                {
                    Operation = EventNames.OPERATION_END,
                    Payload = JsonSerializer.Serialize(formattedComment),
                    InReplyTo = message.MessageId
                };
            }
            catch (MessagingException e)
            {
                error = e.Message;
            }
        }
        else
        {
            error = "Got a corrupted message from Kafka.";
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