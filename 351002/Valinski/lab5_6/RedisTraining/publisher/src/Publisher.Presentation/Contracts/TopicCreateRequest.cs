namespace Publisher.Presentation.Contracts;

public class TopicCreateRequest
{
    public long UserId { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
}
