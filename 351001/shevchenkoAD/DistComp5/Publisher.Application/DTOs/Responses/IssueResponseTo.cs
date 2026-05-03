using System.Text.Json.Serialization;
using Shared.DTOs.Abstractions;
using Shared.DTOs.Responses;

namespace Publisher.Application.DTOs.Responses;

public record IssueResponseTo : BaseResponseTo
{
    [JsonPropertyName("authorId")] public long AuthorId { get; init; }

    [JsonPropertyName("title")] public string Title { get; init; } = null!;

    [JsonPropertyName("content")] public string Content { get; init; } = null!;

    [JsonPropertyName("created")] public DateTime Created { get; init; }

    [JsonPropertyName("modified")] public DateTime Modified { get; init; }

    [JsonPropertyName("comments")] public List<CommentResponseTo>? Comments { get; set; } = new();
}