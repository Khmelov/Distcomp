using System.Text.Json.Serialization;

namespace ArticleHouse.Service.DTOs;

public record CreatorResponseDTO
{
    public required long Id {get; init;}
    public required string Login {get; init;}
    public required string Password {get; init;}
    [JsonPropertyName("firstname")]
    public required string FirstName {get; init;}
    [JsonPropertyName("lastname")]
    public required string LastName {get; init;}
};