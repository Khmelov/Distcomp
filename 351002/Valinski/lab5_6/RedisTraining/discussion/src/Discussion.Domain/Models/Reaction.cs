namespace Discussion.Domain.Models;

public class Reaction
{
    public long TopicId { get; set; }
    public long Id { get; set; }
    public string Country { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
}
