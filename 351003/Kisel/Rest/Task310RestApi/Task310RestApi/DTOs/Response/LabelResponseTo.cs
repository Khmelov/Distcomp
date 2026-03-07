
using System.Text.Json.Serialization;

namespace Task310RestApi.DTOs.Response
{
    public class LabelResponseTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
    }
}