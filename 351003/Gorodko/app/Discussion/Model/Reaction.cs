using System.Text.Json.Serialization;

namespace Discussion.Model {
    public class Reaction {
        [JsonPropertyName("country")]
        public string Country { get; set; } = string.Empty;

        [JsonPropertyName("tweetId")]
        public long TweetId { get; set; }

        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;

        [JsonPropertyName("created")]
        public DateTime Created { get; set; }
    }
}