using System.ComponentModel.DataAnnotations;

public class CommentRequestDto
{
    [Required]
    public long StoryId { get; set; }

    [Required]
    [StringLength(2048, MinimumLength = 2)]
    public string Content { get; set; } = string.Empty;

    public string Country { get; set; } = string.Empty;
}