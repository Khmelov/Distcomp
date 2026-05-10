using System.Text.Json.Serialization;

namespace Shared.DTOs.Responses;

public record AuthResponseTo
{
    public AuthResponseTo()
    {
    }

    public AuthResponseTo(string token)
    {
        AccessToken = token;
    }

    [JsonPropertyName("access_token")] public string AccessToken { get; init; } = null!;

    [JsonPropertyName("token_type")] public string TokenType { get; init; } = "Bearer";
}