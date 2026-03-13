using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Project.Dto {
    public class ReactionRequestTo : BaseRequestTo {
        [Required]
        [JsonPropertyName("tweetId")]
        public long TweetId { get; set; }

        [Required]
        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;
    }
}