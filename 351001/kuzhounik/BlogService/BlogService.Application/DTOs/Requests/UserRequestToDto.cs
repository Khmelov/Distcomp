using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class UserRequestToDto<Id> : IRequestDto<Id>
{    
    [JsonPropertyName("id")]
    public Id ID { get; set; }

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
    
    [JsonPropertyName("role")]
    public string Role { get; set; } = "CUSTOMER";
}