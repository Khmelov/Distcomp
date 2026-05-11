using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using BlogService.Domain.Entities;
using Shared.Application.Interfaces.DTOs;

namespace BlogService.Application.DTOs.Request;

public class CommentRequestToDto<Id> : IRequestDto<Id>
{
    [JsonPropertyName("id")]
    public Id? ID { get; set; }
    
    [JsonPropertyName("storyId")]
    [Required]
    public Id StoryID { get; set; }
    
    [JsonPropertyName("content")]
    [StringLength(2048,  MinimumLength = 2)]
    public string Content { get; set; }
}