namespace Discussion.Presentation.Contracts;

public class UpdateReactionRequest
{
    public long Id { get; set; }
    public string Country { get; set; } = "BY";
    public long TopicId { get; set; }
    public string Content { get; set; } = string.Empty;
}
