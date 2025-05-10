using System;
using System.Text.Json.Serialization;

namespace WebApplication1.DTO
{
    public class TopicRequestTo
    {
        public long? Id { get; set; }
        public long EditorId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
    }

    public class TopicResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

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
    }
}
