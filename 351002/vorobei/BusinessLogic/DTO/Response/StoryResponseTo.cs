using System.Text.Json.Serialization;
using DataAccess.Models;

namespace BusinessLogic.DTO.Response
{
    public class StoryResponseTo : BaseEntity
    {
        [JsonPropertyName("creatorId")]
        public int CreatorId { get; set; }

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
