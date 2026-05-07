using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Response;

public class StickerResponseToDto : IResponseDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("name")]
    public string Name { get; set; }
}