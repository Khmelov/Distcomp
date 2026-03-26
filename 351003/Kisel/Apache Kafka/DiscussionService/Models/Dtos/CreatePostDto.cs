using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Models.Dtos;

public class CreatePostDto
{
    [Required]
    public int NewsId { get; set; }

    [Required]
    [StringLength(2048, MinimumLength = 2)]
    public string Content { get; set; } = string.Empty;
}