using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class StoryRequestToDto<Id> : IRequestDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("userId")]
    [Required]
    public Id UserID { get; set; }
    
    [JsonPropertyName("title")]
    [StringLength(64, MinimumLength = 2)]
    public string Title { get; set; }
    
    [JsonPropertyName("content")]
    [StringLength(2048, MinimumLength = 4)]
    public string Content { get; set; }
}