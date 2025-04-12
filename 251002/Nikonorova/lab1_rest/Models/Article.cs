using System.Text.Json.Serialization;

namespace distcomp.Models
{
    public class Article
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
