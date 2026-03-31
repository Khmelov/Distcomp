namespace Application.Dtos;

public class TopicUpdateDto
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public string Title { get; set; }
    public string Content { get; set; }
}
