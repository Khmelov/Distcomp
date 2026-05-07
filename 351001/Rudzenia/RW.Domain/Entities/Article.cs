namespace RW.Domain.Entities;

public class Article
{
    public long Id { get; set; }
    public long AuthorId { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
    public ICollection<Tag> Tags { get; set; } = new List<Tag>();
}
