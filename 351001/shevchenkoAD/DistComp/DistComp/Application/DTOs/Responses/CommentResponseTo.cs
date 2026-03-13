using System.Text.Json.Serialization;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Responses;

public record CommentResponseTo(
    long Id,
    [property: JsonPropertyName("issueId")]
    long IssueId,
    [property: JsonPropertyName("content")]
    string Content
)
    : BaseResponseTo(Id);