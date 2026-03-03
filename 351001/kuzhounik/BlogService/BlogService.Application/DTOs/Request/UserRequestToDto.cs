using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class UserRequestToDto : IRequestDto<long>
{    
    [JsonPropertyName("id")]
    public long ID { get; set; }

    [JsonPropertyName("login")]
    [StringLength(64, MinimumLength = 2)]
    public string Login { get; set; }
    
    [JsonPropertyName("password")]
    [StringLength(128, MinimumLength = 8)]
    public string Password { get; set; }
    
    [JsonPropertyName("firstname")]
    [StringLength(64, MinimumLength = 2)]
    public string FirstName { get; set; }
    
    [JsonPropertyName("lastname")]
    [StringLength(64, MinimumLength = 2)]
    public string LastName { get; set; }
}