using System.Text.Json;
using Additions.Service;
using Additions.Service.EventService.Interfaces;
using CommentMicroservice.Service.DTOs;
using CommentMicroservice.Service.Interfaces;
using CommonAPI.Service.Events;

namespace CommentMicroservice.Service.Implementations.EventHandlers;

public class ArticleDeletedHandler : IEventHandler
{
    private readonly ICommentService commentService;
    private readonly IEventProducerService producerService;
    private readonly string eventTopic;

    public string SupportedOperation
    {
        get
        {
            return EventNames.ARTICLE_DELETED;
        }
    }

    public ArticleDeletedHandler(ICommentService commentService, IEventProducerService producerService,
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