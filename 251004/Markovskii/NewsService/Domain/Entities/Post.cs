namespace Domain.Entities;

public class Post
{
    public long Id { get; set; }
    public long NewsId { get; set; }
    public string Content { get; set; }
}