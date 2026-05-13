namespace Discussion.Presentation.Contracts;

public class ReactionRequest
{
    public required string Content { get; set; }
    public required string Country { get; set; }
    public required long TopicId { get; set; }
    public required long Id { get; set; }
}
