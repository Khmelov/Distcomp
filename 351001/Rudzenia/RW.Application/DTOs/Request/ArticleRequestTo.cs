using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Request;

public class ArticleRequestTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("authorId")]
    public long AuthorId { get; set; }

    [JsonPropertyName("title")]
    public string Title { get; set; } = string.Empty;

    [JsonPropertyName("content")]
    public string Content { get; set; } = string.Empty;

    [JsonPropertyName("tags")]
    public List<string> Tags { get; set; } = new();
}
