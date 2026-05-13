namespace Shared.Messages;

public class ReactionResponseMsg
{
    public required long Id { get; set; }
    public required long TopicId { get; set; }
    public required string Content { get; set; }
    public required string Country { get; set; }
    public ReactionStatuses? Status { get; set; } = null;
}
