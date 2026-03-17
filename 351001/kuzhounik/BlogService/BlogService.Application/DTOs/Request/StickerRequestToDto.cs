using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using BlogService.Application.DependencyInjection.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class StickerRequestToDto : IRequestDto<long>
{
    [JsonPropertyName("id")]
    public long ID { get; set; }
    
    [JsonPropertyName("name")]
    [StringLength(32, MinimumLength = 2)]
    public string Name { get; set; }
}