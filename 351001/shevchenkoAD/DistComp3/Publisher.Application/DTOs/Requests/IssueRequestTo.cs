using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Publisher.Application.DTOs.Abstractions;

namespace Publisher.Application.DTOs.Requests;

public record IssueRequestTo : BaseRequestTo
{
    public IssueRequestTo() { }

    [Required]
    [JsonPropertyName("authorId")]
    public long AuthorId { get; init; }

    [Required]
    [StringLength(64, MinimumLength = 2)]
    [JsonPropertyName("title")]
    public string Title { get; init; } = null!;

    [Required]
    [StringLength(2048, MinimumLength = 4)]
    [JsonPropertyName("content")]
    public string Content { get; init; } = null!;

    [JsonPropertyName("labels")]
    public List<string>? Labels { get; init; }
}