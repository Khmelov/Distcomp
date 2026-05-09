using System.Text.Json.Serialization;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Responses;

public record CommentResponseTo(
    long Id,
    [property: JsonPropertyName("issueId")]
    long IssueId,
    [property: JsonPropertyName("content")]
    string Content
)
    : BaseResponseTo(Id);