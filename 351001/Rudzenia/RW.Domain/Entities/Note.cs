namespace RW.Domain.Entities;

public class Note
{
    public long Id { get; set; }
    public long ArticleId { get; set; }
    public string Content { get; set; } = string.Empty;
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
}
