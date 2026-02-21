using System.Text.Json.Serialization;

namespace Task310RestApi.DTOs.Response
{
    public class PostResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("newsId")]
        public long NewsId { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;

        [JsonPropertyName("created")]
        public DateTime Created { get; set; }

        [JsonPropertyName("modified")]
        public DateTime Modified { get; set; }
    }
}