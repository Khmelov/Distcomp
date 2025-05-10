using Distributed_Computing_Lab_1.Models;

namespace Distributed_Computing_Lab_1.DTO.ResponseDTO;

public class MessageResponseDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    public Topic Topic { get; set; }
    
    public string Content { get; set; }
}