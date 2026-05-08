using System.Text.Json.Serialization;

namespace Task310RestApi.DTOs.Response
{
    public class CreatorResponseTo
    {
        [JsonPropertyName("id")]
public long Id { get; set; }

[JsonPropertyName("login")]
public string Login { get; set; } = string.Empty;

[JsonPropertyName("password")]
public string Password { get; set; } = string.Empty;

[JsonPropertyName("firstname")]
public string Firstname { get; set; } = string.Empty;

[JsonPropertyName("lastname")]
public string Lastname { get; set; } = string.Empty;

[JsonPropertyName("created")]
public DateTime Created { get; set; }

[JsonPropertyName("modified")]
public DateTime Modified { get; set; }
}
}