using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class StoryRequestToDto : IRequestDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("userId")]
    [Required]
    public long UserID { get; set; }
    
    [JsonPropertyName("title")]
    [StringLength(64, MinimumLength = 2)]
    public string Title { get; set; }
    
    [JsonPropertyName("content")]
    [StringLength(2048, MinimumLength = 4)]
    public string Content { get; set; }
}