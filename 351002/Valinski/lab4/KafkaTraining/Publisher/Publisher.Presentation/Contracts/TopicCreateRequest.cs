namespace Publisher.Presentation.Contracts;

public class TopicCreateRequest
{
    public long UserId { get; set; }
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
}
