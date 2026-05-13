using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class StoryResponseToDto<Id> : IResponseDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("userId")]
    public Id UserID { get; set; }
    
    [JsonPropertyName("title")]
    public string Title { get; set; }
    
    [JsonPropertyName("content")]
    public string Content { get; set; }
}