namespace Publisher.Presentation.Contracts;

public class ReactionResponse
{
    public long Id { get; set; }
    public string Country { get; set; } = null!;
    public long TopicId { get; set; }
    public string Content { get; set; } = null!;
}
