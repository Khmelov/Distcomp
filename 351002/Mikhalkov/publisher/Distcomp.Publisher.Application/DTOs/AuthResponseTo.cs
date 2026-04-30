using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record AuthResponseTo(
        [property: JsonPropertyName("access_token")] string AccessToken,
        [property: JsonPropertyName("token_type")] string TokenType = "Bearer"
    );
}