using Distributed_Computing_Lab_2.Models;

namespace Distributed_Computing_Lab_2.DTO.ResponseDTO;

public class TopicResponseDTO
{
    public long Id { get; set; }
    public string Title { get; set; }
    
    public long AuthorId { get; set; }
    public Author Author { get; set; }

    public List<Message> Notices { get; set; }
    
    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public List<Sticker> Tags { get; set; } = [];

}