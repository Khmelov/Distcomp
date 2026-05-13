using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class UserResponseToDto<Id> : IResponseDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("login")]
    public string Login { get; set; }
    
    [JsonPropertyName("password")]
    public string Password { get; set; }
    
    [JsonPropertyName("firstname")]
    public string FirstName { get; set; }
    
    [JsonPropertyName("lastname")]
    public string LastName { get; set; }

    [JsonPropertyName("role")] 
    public string Role { get; set; } = string.Empty;
}