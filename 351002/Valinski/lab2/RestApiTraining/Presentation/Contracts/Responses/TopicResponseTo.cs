namespace Presentation.Contracts.Responses;

public class TopicResponseTo
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public string Title { get; set; }
    public string Content { get; set; }
    public DateTime CreatedAt { get; set; }
    public DateTime ModifiedAt { get; set; }
}
