using Distributed_Computing_Lab_1.Models;

namespace Distributed_Computing_Lab_1.DTO.ResponseDTO;

public class TopicResponseDTO
{
    public long Id { get; set; }
    public string Title { get; set; }
    
    // Заменяем UserId на AuthorId
    public long AuthorId { get; set; }
    public Author Author { get; set; }  // Обновляем также ссылку на автора

    public List<Message> Notices { get; set; }
    
    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public List<Sticker> Tags { get; set; } = new List<Sticker>();  // Обновление для корректной инициализации
}
