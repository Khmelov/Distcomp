using System.Text.Json;
using Additions.Messaging.Interfaces;
using Additions.Service;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using CommonAPI.Service.Events;

namespace ArticleHouse.Service.Implementations;

public class CommentService : BasicService, ICommentService
{
    private readonly IEventProducer eventProducer;
    private readonly string eventTopic;
    
    public CommentService(IEventProducer eventProducer, IConfiguration configuration)
    {
        this.eventProducer = eventProducer;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }
    public async Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto)
    {
        CommentPayload model = MakePayloadFromRequest(dto);
        EventMessage message = new()
        {
            Operation = EventNames.COMMENT_ADD,
            Payload = JsonSerializer.Serialize(model)
        };
        var result = await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, message));
        return MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
    }

    public async Task DeleteCommentAsync(long id)
    {
        await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, new EventMessage()
        {
            Operation = EventNames.COMMENT_DELETE,
            Payload = JsonSerializer.Serialize(id)
        }));
    }

    public async Task<CommentResponseDTO[]> GetAllCommentsAsync()
    {
        EventMessage message = new()
        {
            Operation = EventNames.MANY_COMMENTS_GET
        };
        var result = await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, message));
        return [.. result.GetPayload<ManyCommentsPayload>()!.Comments.Select(MakeResponseFromPayload)];
    }

    public async Task<CommentResponseDTO> GetCommentByIdAsync(long id)
    {
        EventMessage message = new()
        {
            Operation = EventNames.COMMENT_GET,
            Payload = JsonSerializer.Serialize(id)
        };
        var result = await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, message));
        return MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
    }

    public async Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto)
    {
        CommentPayload model = MakePayloadFromRequest(dto);
        model.Id = id;
        EventMessage message = new()
        {
            Operation = EventNames.COMMENT_UPDATE,
            Payload = JsonSerializer.Serialize(model)
        };
        var result = await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, message));
        return MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
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