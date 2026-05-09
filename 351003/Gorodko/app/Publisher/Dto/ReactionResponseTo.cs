using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class ReactionResponseTo : BaseResponseTo {
        [JsonPropertyName("tweetId")]
        public long TweetId { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;
    }
}