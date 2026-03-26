namespace Application.Dtos;

public class TopicCreateDto
{
    public long UserId { get; init; }
    public string Title { get; init; }
    public string Content { get; init; }
}
