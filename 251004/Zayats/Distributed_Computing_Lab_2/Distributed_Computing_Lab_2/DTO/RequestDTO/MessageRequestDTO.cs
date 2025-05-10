namespace Distributed_Computing_Lab_2.DTO.RequestDTO;

public class MessageRequestDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    
    public string Content { get; set; }
}