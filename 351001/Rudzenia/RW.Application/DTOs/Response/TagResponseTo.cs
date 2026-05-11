using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Response;

public class TagResponseTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;
}
