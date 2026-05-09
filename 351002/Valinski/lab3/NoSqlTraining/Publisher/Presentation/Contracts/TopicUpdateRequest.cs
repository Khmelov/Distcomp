namespace Presentation.Contracts;

public class TopicUpdateRequest
{
    public long Id { get; set; }
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
}
