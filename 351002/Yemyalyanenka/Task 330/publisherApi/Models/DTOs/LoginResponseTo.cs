using System.Text.Json.Serialization;

namespace publisherApi.Models.DTOs
{
    public record LoginResponseTo(
        [property: JsonPropertyName("access_token")] string AccessToken,
        [property: JsonPropertyName("token_type")] string TokenType = "Bearer");
}
