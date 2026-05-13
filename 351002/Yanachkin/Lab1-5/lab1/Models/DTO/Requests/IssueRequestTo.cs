using System.Text.Json.Serialization;

namespace lab1.Models.DTO.Requests
{
    public class IssueRequestTo
    {
        public long Id { get; set; }
        public long EditorId { get; set; }

        public string Title { get; set; } = null!;

        public string Content { get; set; } = null!;

        public List<long> LabelIds { get; set; } = [];

        /// <summary>Имена меток (JSON: labelNames). Несуществующие строки создаются в tbl_label.</summary>
        public List<string> LabelNames { get; set; } = [];

        /// <summary>Имена меток (JSON: labels). То же поведение, что у LabelNames.</summary>
        [JsonPropertyName("labels")]
        public List<string>? Labels { get; set; }
    }
}
