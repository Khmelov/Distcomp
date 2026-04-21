using Newtonsoft.Json;

namespace Task310RestApi.DTOs.Response
{
    public class NewsResponseTo
    {
        [JsonProperty("id")]
        public long Id { get; set; }

        [JsonProperty("creatorId")]
        public long CreatorId { get; set; }

        [JsonProperty("title")]
        public string Title { get; set; } = string.Empty;

        [JsonProperty("content")]
        public string Content { get; set; } = string.Empty;

        [JsonProperty("created")]
        public DateTime Created { get; set; }

        [JsonProperty("modified")]
        public DateTime Modified { get; set; }

        [JsonProperty("labelIds")]
        public List<long> LabelIds { get; set; } = new List<long>();
    }
}