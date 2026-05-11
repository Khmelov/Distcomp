using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class StickerResponseToDto<Id> : IResponseDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("name")]
    public string Name { get; set; }
}