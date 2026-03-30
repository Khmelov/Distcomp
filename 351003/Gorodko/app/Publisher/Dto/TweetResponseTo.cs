using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class TweetResponseTo : BaseResponseTo {
        [JsonPropertyName("editorId")]
        public long EditorId { get; set; }

        [JsonPropertyName("title")]
        public string Title { get; set; } = string.Empty;

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;

        [JsonPropertyName("created")]
        public DateTime Created { get; set; }

        [JsonPropertyName("modified")]
        public DateTime Modified { get; set; }

        [JsonPropertyName("stickers")]
        public List<string>? Stickers { get; set; }
    }
}