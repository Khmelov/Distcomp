namespace DiscussionService.Models.Entities;

public class Post
{
    public int Id { get; set; }
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
    public string State { get; set; } = "PENDING"; // Новое поле
    public DateTime Created { get; set; }
}