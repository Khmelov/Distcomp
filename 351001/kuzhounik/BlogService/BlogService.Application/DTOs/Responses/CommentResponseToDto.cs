using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class CommentResponseToDto<Id> : IResponseDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("storyId")]
    public Id StoryID { get; set; }
    
    [JsonPropertyName("content")]
    public string Content { get; set; }
}