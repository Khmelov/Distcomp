using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class CommentResponseToDto : IResponseDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("storyId")]
    public long StoryID { get; set; }
    
    [JsonPropertyName("content")]
    public string Content { get; set; }
}