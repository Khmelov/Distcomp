namespace DiscussionService.Models.Dtos;

public class PostDto
{
    public int Id { get; set; } // Изменено на int
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
}