namespace ServerApp.Models.Entities;

public class Article : BaseEntity
{
    public long AuthorId { get; set; }
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
}