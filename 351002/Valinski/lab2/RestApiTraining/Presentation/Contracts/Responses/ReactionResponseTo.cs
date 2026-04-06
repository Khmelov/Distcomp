namespace Presentation.Contracts.Responses;

public class ReactionResponseTo
{
    public long Id { get; init; }
    public long TopicId { get; init; }
    public string Content { get; init; } = string.Empty;
}
