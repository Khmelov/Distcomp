namespace RW.Domain.Entities;

public class Tag
{
    public long Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public ICollection<Article> Articles { get; set; } = new List<Article>();
}
