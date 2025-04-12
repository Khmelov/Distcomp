using System.Text.Json.Serialization;

namespace distcomp.DTOs
{
    public class ArticleResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }
        [JsonPropertyName("creatorId")]
        public long CreatorId { get; set; }
        [JsonPropertyName("title")]
        public string Title { get; set; }
        [JsonPropertyName("content")]
        public string Content { get; set; }
        [JsonPropertyName("created")]
        public DateTime Created { get; set; }
        [JsonPropertyName("modified")]
        public DateTime Modified { get; set; } 
    }
}
