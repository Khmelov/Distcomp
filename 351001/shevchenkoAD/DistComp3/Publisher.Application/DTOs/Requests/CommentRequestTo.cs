using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Publisher.Application.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Requests;

public record CommentRequestTo : BaseRequestTo
{
    public CommentRequestTo() { }

    [Required]
    [JsonPropertyName("issueId")]
    public long IssueId { get; init; }

    [Required]
    [StringLength(2048, MinimumLength = 2)]
    [JsonPropertyName("content")]
    public string Content { get; init; } = null!;
}