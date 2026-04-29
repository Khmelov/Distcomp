namespace ServerApp.Models.Entities;

public class Message : BaseEntity
{
    public long ArticleId { get; set; }
    public string Content { get; set; } = null!;
}