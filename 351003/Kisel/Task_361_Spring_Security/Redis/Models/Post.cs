namespace Redis.Models;

// Post хранится в Cassandra, поэтому атрибут Table не нужен
public class Post
{
    public int Id { get; set; }
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
}