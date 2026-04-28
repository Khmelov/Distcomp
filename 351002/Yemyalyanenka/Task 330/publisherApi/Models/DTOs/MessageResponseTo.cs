using System.Text.Json.Serialization;

namespace RestApiTask.Models.DTOs
{
    public class MessageResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }
        [JsonPropertyName("articleId")]
        public long ArticleId { get; set; }
        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;
    }
}
