namespace Publisher.Domain.Models;

public class Topic
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public User? User { get; set; }
    public List<Label> Labels { get; set; } = new List<Label>();
}
