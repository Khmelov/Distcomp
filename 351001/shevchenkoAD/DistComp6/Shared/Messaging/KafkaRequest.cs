using Shared.DTOs.Requests;

namespace Shared.Messaging;

public record KafkaRequest
{
    public string Method { get; init; } = null!;

    public CommentRequestTo Payload { get; init; } = null!;

    public string CorrelationId { get; init; } = null!;

    public string? UserLogin { get; init; }
}