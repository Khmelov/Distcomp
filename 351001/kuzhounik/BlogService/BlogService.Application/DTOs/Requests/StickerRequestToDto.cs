using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class StickerRequestToDto<Id> : IRequestDto<Id>
{
    [JsonPropertyName("id")]
    public Id ID { get; set; }
    
    [JsonPropertyName("name")]
    [StringLength(32, MinimumLength = 2)]
    public string Name { get; set; }
}