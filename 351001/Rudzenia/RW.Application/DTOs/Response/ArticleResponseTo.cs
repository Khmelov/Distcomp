using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Response;

public class ArticleResponseTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("authorId")]
    public long AuthorId { get; set; }

    [JsonPropertyName("title")]
    public string Title { get; set; } = string.Empty;

    [JsonPropertyName("content")]
    public string Content { get; set; } = string.Empty;

    [JsonPropertyName("created")]
    public DateTime Created { get; set; }

    [JsonPropertyName("modified")]
    public DateTime Modified { get; set; }
}
