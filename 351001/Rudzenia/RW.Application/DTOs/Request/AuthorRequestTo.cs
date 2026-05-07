using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Request;

public class AuthorRequestTo
{
    [JsonPropertyName("id")]
    public long Id { get; set; }

    [JsonPropertyName("login")]
    public string Login { get; set; } = string.Empty;

    [JsonPropertyName("password")]
    public string Password { get; set; } = string.Empty;

    [JsonPropertyName("firstname")]
    public string FirstName { get; set; } = string.Empty;

    [JsonPropertyName("lastname")]
    public string LastName { get; set; } = string.Empty;
}
