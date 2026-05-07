namespace Discussion.Domain.Models;

public class Reaction
{
    public long Id { get; set; }
    public string Country { get; set; } = "BY";
    public long TopicId { get; set; }
    public string Content { get; set; } = null!;
}
