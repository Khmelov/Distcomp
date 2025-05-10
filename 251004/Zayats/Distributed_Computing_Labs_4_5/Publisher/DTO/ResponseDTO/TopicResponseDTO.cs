using Publisher.Models;

namespace Publisher.DTO.ResponseDTO;

public class TopicResponseDTO
{
    public long Id { get; set; }
    public string Title { get; set; }
    
    public long AuthorId { get; set; }
    public Author Author { get; set; }

    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public List<Sticker> Tags { get; set; } = [];
}