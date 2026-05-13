namespace Shared.Messages;

public class CreateReactionRequestMsg
{
    public required long Id { get; set; }
    public required long TopicId { get; set; }
    public required string Content { get; set; }
    public required string Country { get; set; }
}
