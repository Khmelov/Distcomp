using System.Text.Json.Serialization;

namespace RW.Application.DTOs.Request;

public class LoginRequestTo
{
    [JsonPropertyName("login")]
    public string Login { get; set; } = string.Empty;

    [JsonPropertyName("password")]
    public string Password { get; set; } = string.Empty;
}
