using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class TweetRequestTo : BaseRequestTo {
        [Required]
        [JsonPropertyName("editorId")]
        public long EditorId { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("title")]
        public string Title { get; set; } = string.Empty;

        [Required]
        [StringLength(2048, MinimumLength = 4)]
        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;
        [JsonPropertyName("stickers")]
        public List<string>? Stickers { get; set; }
    }
}