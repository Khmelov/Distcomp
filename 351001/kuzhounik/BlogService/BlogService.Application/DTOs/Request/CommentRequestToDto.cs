using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class CommentRequestToDto : IRequestDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("storyId")]
    [Required]
    public long StoryID { get; set; }
    
    [JsonPropertyName("content")]
    [StringLength(2048,  MinimumLength = 2)]
    public string Content { get; set; }
}