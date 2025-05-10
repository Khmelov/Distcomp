namespace Publisher.DTO.RequestDTO;

public class MessageRequestDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    
    public string Content { get; set; }
}