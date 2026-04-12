namespace Presentation.Contracts;

public class CreateReactionRequest
{
    public string Country { get; set; } = "BY";
    public long TopicId { get; set; }
    public string Content { get; set; } = string.Empty;
}
