namespace Presentation.Contracts.Requests;

public class ReactionUpdateRequestTo
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public string Content { get; set; } = string.Empty;
}
