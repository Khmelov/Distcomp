using System.Text.Json.Serialization;

namespace RW.Discussion.DTOs;

public class NoteRequestTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("articleId")]
    public long ArticleId { get; set; }

    [JsonPropertyName("content")]
    public string Content { get; set; } = string.Empty;

    [JsonPropertyName("firstname")]
    public string FirstName { get; set; } = string.Empty;

    [JsonPropertyName("lastname")]
    public string LastName { get; set; } = string.Empty;
}
