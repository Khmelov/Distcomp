namespace Shared.Domain.DTOs;

public class CommentKafkaMessage
{
    public long ID { get; set; }
    public long StoryID { get; set; }
    public string Text { get; set; }
    public string State { get; set; } // PENDING, APPROVE, DECLINE
}