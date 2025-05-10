namespace Discussion.DTO.Response;

public class MessageResponseDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    
    public string Content { get; set; }
}