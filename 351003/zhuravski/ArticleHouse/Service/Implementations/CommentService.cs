using Additions.Service;
using Additions.Service.EventService;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using CommonAPI.Service.Events;

namespace ArticleHouse.Service.Implementations;

public class CommentService : BasicService, ICommentService
{
    private readonly IEventProducerService producerService;
    private readonly string eventTopic;
    private readonly TimeSpan operationTimeout = TimeSpan.FromSeconds(5);
    
    public CommentService(IEventProducerService producerService, IConfiguration configuration)
    {
        this.producerService = producerService;
        eventTopic = configuration["Kafka:RecvTopic"] ?? "default-topic";
    }
    public async Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto)
    {
        CommentPayload model = MakePayloadFromRequest(dto);
        EventMessage<CommentPayload> message = new()
        {
            Operation = EventNames.COMMENT_ADD,
            Payload = model
        };
        var result = await producerService.ProduceEventWithResponseAsync<CommentPayload, CommentPayload>(eventTopic, message, operationTimeout);
        return MakeResponseFromPayload(result.Payload);
    }

    public async Task DeleteCommentAsync(long id)
    {
        await producerService.ProduceEventAsync(eventTopic, new EventMessage<long>()
        {
            Operation = EventNames.COMMENT_DELETE,
            Payload = id
        });
    }

    public async Task<CommentResponseDTO[]> GetAllCommentsAsync()
    {
        EventMessage<object> message = new()
        {
            Operation = EventNames.MANY_COMMENTS_GET
        };
        var result = await producerService.ProduceEventWithResponseAsync<object, ManyCommentsPayload>(eventTopic, message, operationTimeout);
        return [.. result.Payload.Comments.Select(MakeResponseFromPayload)];
    }

    public async Task<CommentResponseDTO> GetCommentByIdAsync(long id)
    {
        EventMessage<long> message = new()
        {
            Operation = EventNames.COMMENT_GET,
            Payload = id
        };
        var result = await producerService.ProduceEventWithResponseAsync<long, CommentPayload>(eventTopic, message, operationTimeout);
        return MakeResponseFromPayload(result.Payload);
    }

    public async Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto)
    {
        CommentPayload model = MakePayloadFromRequest(dto);
        model.Id = id;
        EventMessage<CommentPayload> message = new()
        {
            Operation = EventNames.COMMENT_UPDATE,
            Payload = model
        };
        var result = await producerService.ProduceEventWithResponseAsync<CommentPayload, CommentPayload>(eventTopic, message, operationTimeout);
        return MakeResponseFromPayload(result.Payload);
    }

    private static CommentPayload MakePayloadFromRequest(CommentRequestDTO dto)
    {
        CommentPayload result = new();
        ShapePayloadFromRequest(ref result, dto);
        return result;
    }

    private static void ShapePayloadFromRequest(ref CommentPayload model, CommentRequestDTO dto)
    {
        model.Id = dto.Id ?? 0;
        model.ArticleId = dto.ArticleId;
        model.Content = dto.Content;
    }

    private static CommentResponseDTO MakeResponseFromPayload(CommentPayload model)
    {
        return new CommentResponseDTO()
        {
            Id = model.Id,
            ArticleId = model.ArticleId,
            Content = model.Content
        };
    }
}