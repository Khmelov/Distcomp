namespace Publisher.Presentation.Contracts;

public class TopicUpdateRequest
{
    public long Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
}
