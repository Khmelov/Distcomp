using System.Text.Json;
using Additions.Cache.Interfaces;
using Additions.Messaging.Interfaces;
using Additions.Service;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using CommonAPI.Service.Events;

namespace ArticleHouse.Service.Implementations;

public class CommentService : BasicService, ICommentService
{
    private readonly IEventProducer eventProducer;
    private readonly IDistributedCache cache;
    private readonly string eventTopic;
    
    public CommentService(IEventProducer eventProducer, IDistributedCache cache, IConfiguration configuration)
    {
        this.eventProducer = eventProducer;
        this.cache = cache;
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
        var response = MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
        
        await cache.RemoveAsync($"comments:article:{dto.ArticleId}:all");
        
        return response;
    }

    public async Task DeleteCommentAsync(long id)
    {
        var existing = await GetCommentByIdAsync(id);
        
        await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, new EventMessage()
        {
            Operation = EventNames.COMMENT_DELETE,
            Payload = JsonSerializer.Serialize(id)
        }));
        
        await cache.RemoveAsync($"comment:{id}");
        await cache.RemoveAsync($"comments:article:{existing.ArticleId}:all");
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
        return await cache.GetOrSetAsync(
            $"comment:{id}",
            async () =>
            {
                EventMessage message = new()
                {
                    Operation = EventNames.COMMENT_GET,
                    Payload = JsonSerializer.Serialize(id)
                };
                var result = await InvokeLowerMethod(() => eventProducer.ProduceEventWithResponseAsync(eventTopic, message));
                return MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
            },
            TimeSpan.FromMinutes(10)
        );
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
        var response = MakeResponseFromPayload(result.GetPayload<CommentPayload>()!);
        
        await cache.RemoveAsync($"comment:{id}");
        await cache.RemoveAsync($"comments:article:{dto.ArticleId}:all");
        
        return response;
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