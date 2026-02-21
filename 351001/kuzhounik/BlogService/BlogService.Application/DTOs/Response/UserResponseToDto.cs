using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class UserResponseToDto : IResponseDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("login")]
    public string Login { get; set; }
    
    [JsonPropertyName("password")]
    public string Password { get; set; }
    
    [JsonPropertyName("firstname")]
    public string FirstName { get; set; }
    
    [JsonPropertyName("lastname")]
    public string LastName { get; set; }
}