using System.Text.Json.Serialization;

namespace WebApplication1.DTO
{
    public class TagRequestTo
    {
        public long? Id { get; set; }
        public string Name { get; set; } = string.Empty;
    }

    public class TagResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
    }
}
