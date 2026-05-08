using System.Text.Json.Serialization;
using Publisher.Application.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Responses;

public record CommentResponseTo : BaseResponseTo
{
    public CommentResponseTo() { }

    [JsonPropertyName("issueId")]
    public long IssueId { get; init; }

    [JsonPropertyName("content")]
    public string Content { get; init; } = null!;
}