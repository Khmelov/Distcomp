using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class StoryResponseToDto : IResponseDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("userId")]
    public long UserID { get; set; }
    
    [JsonPropertyName("title")]
    public string Title { get; set; }
    
    [JsonPropertyName("content")]
    public string Content { get; set; }
}