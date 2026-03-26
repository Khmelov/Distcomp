namespace DiscussionService.Models.Dtos;

public class PostDto
{
    public int Id { get; set; }
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
    public string State { get; set; } = "PENDING";
}