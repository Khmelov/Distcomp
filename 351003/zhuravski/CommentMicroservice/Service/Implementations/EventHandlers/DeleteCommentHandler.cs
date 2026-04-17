using Additions.Messaging;
using Additions.Messaging.Interfaces;
using CommentMicroservice.Service.Interfaces;
using CommonAPI.Messaging;

namespace CommentMicroservice.Service.Implementations.EventHandlers;

public class DeleteCommentHandler : IEventHandler
{
    private readonly ICommentService commentService;
    private readonly IEventProducer producerService;
    private readonly string eventTopic;

    public string SupportedOperation
    {
        get
        {
            return EventNames.COMMENT_DELETE;
        }
    }

    public DeleteCommentHandler(ICommentService commentService, IEventProducer producerService,
                                    IConfiguration configuration)
    {
        this.commentService = commentService;
        this.producerService = producerService;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }

    public async Task HandleMessage(EventMessage message)
    {
        EventMessage response = null!;
        long? payload = message.GetPayload<long>();
        string? error = null;
        if (payload != null) {
            try
            {
                await commentService.DeleteCommentAsync(payload.Value);
                response = new()
                {
                    Operation = EventNames.OPERATION_END,
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