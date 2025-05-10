using Distributed_Computing_Lab_2.Models;

namespace Distributed_Computing_Lab_2.DTO.ResponseDTO;

public class MessageResponseDTO
{
    public long Id { get; set; }
    
    public long TopicId { get; set; }
    public Topic Topic { get; set; }
    
    public string Content { get; set; }
}