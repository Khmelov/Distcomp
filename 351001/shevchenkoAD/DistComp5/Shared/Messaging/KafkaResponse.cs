using Shared.DTOs.Responses;

namespace Shared.Messaging;

public record KafkaResponse
{
    public bool IsSuccess { get; init; }
    public CommentResponseTo? Payload { get; init; }
    public List<CommentResponseTo>? PayloadList { get; init; } 
    public string? ErrorMessage { get; init; }
    public int? ErrorSubCode { get; init; }
    public string CorrelationId { get; init; } = null!;
}