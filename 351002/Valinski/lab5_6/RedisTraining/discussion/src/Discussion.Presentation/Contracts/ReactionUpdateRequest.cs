namespace Discussion.Presentation.Contracts;

public class ReactionUpdateRequest
{
    public required long Id { get; set; }
    public required long TopicId { get; set; }
    public required string Content { get; set; }
    public string? Country { get; set; }
}
