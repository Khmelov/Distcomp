using Publisher.Models;

namespace Publisher.DTO.ResponseDTO;

public class MessageResponseDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    public Topic Topic { get; set; }
    
    public string Content { get; set; }
}