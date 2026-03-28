using System.Text.Json.Serialization;

namespace Discussion.DTO {
    public class ReactionRequestTo {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("tweetId")]
        public long TweetId { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;

        [JsonPropertyName("country")]
        public string Country { get; set; } = "by";
    }
}