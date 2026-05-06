namespace Shared.Messages;

public class UpdateReactionRequestMsg
{
    public required long  Id { get; set; }
    public long TopicId { get; set; }
    public string? Content { get; set; }
    public string? Country { get; set; }
}
