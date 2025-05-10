namespace Distributed_Computing_Lab_1.Models;

public class Topic : BaseModel
{
    public string Title { get; set; }

    // Переименовываем UserId в AuthorId
    public long AuthorId { get; set; }

    // Вместо Author теперь используем Author для связи с пользователем
    public Author Author { get; set; }

    public List<Message> Notices { get; set; } = new List<Message>();

    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public List<Sticker> Tags { get; set; } = new List<Sticker>();
}
