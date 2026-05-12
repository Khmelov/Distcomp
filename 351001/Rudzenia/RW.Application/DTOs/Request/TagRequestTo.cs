using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Request;

public class TagRequestTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;
}
