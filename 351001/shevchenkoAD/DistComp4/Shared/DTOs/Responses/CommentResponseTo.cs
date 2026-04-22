using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;
using Shared.Enums;

namespace Shared.DTOs.Responses;

public record CommentResponseTo : BaseResponseTo
{
    [JsonPropertyName("issueId")] public long IssueId { get; init; }

    [JsonPropertyName("content")] public string Content { get; init; } = null!;

    [JsonPropertyName("state")] public CommentState State { get; init; }
}