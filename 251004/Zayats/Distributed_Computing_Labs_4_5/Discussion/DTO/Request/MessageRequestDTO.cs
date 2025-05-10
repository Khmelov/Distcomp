namespace Discussion.DTO.Request;

public class MessageRequestDTO
{
    public long Id { get; set; }

    public long TopicId { get; set; }

    public string Content { get; set; }
}