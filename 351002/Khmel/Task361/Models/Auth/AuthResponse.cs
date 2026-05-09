using System.Text.Json.Serialization;

public class AuthResponse
{
    [JsonPropertyName("access_token")]
    public string AccessToken { get; set; } = "";
    
    [JsonPropertyName("tokenType")]
    public string TokenType { get; set; } = "Bearer";
}