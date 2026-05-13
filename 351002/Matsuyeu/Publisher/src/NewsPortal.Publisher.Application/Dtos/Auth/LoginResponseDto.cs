using System.Text.Json.Serialization;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.Auth
{
    public class LoginResponseDto
    {
        [JsonPropertyName("access_token")]
        public string AccessToken { get; set; } = "";

        [JsonPropertyName("tokenType")]
        public string TokenType { get; set; } = "Bearer";
    }
}
