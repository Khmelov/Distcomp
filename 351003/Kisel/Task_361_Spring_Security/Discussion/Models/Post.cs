namespace Discussion.Models;

public class Post
{
    public int Id { get; set; }
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
}