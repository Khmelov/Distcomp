using System.Text.Json.Serialization;

namespace Distcomp.Shared.Models
{
    public class Note
    {
        [JsonPropertyName("country")]
        public string Country { get; set; } = "BY";
        [JsonPropertyName("issueId")]
        public long IssueId { get; set; }
        [JsonPropertyName("id")]
        public long Id { get; set; }
        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;
        [JsonPropertyName("state")]
        public NoteState State { get; set; } = NoteState.PENDING;
    }
}