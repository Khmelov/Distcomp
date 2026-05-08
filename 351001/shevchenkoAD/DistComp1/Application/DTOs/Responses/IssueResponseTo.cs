using System.Text.Json.Serialization;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Responses;

public record IssueResponseTo(
    long Id,
    [property: JsonPropertyName("authorId")]
    long AuthorId,
    [property: JsonPropertyName("title")] string Title,
    [property: JsonPropertyName("content")]
    string Content,
    [property: JsonPropertyName("created")]
    DateTime Created,
    [property: JsonPropertyName("modified")]
    DateTime Modified
)
    : BaseResponseTo(Id);