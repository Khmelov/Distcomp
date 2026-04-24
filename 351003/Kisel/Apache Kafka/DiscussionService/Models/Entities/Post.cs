namespace DiscussionService.Models.Entities;

public class Post
{
    public int Id { get; set; } // Изменено на int
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
    public DateTime Created { get; set; }
}