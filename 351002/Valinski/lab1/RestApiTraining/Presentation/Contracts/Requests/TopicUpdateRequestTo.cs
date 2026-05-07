namespace Presentation.Contracts.Requests;

public class TopicUpdateRequestTo
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public string Title { get; set; }
    public string Content { get; set; }
}
