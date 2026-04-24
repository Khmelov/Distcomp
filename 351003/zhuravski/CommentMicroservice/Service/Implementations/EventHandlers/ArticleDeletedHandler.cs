using Additions.Messaging.Interfaces;
using Additions.Service;
using CommentMicroservice.Service.Interfaces;
using CommonAPI.Messaging;

namespace CommentMicroservice.Service.Implementations.EventHandlers;

public class ArticleDeletedHandler : IEventHandler
{
    private readonly ICommentService commentService;
    private readonly IEventProducer producerService;
    private readonly string eventTopic;

    public string SupportedOperation
    {
        get
        {
            return EventNames.ARTICLE_DELETED;
        }
    }

    public ArticleDeletedHandler(ICommentService commentService, IEventProducer producerService,
                                    IConfiguration configuration)
    {
        this.commentService = commentService;
        this.producerService = producerService;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }

    public async Task HandleMessage(EventMessage message)
    {
        long? payload = message.GetPayload<long>();
        if (payload != null) {
            try
            {
                await commentService.DeleteCommentsByArticleIdAsync(payload.Value);
            }
            catch (ServiceException) {}
        }
    }
}