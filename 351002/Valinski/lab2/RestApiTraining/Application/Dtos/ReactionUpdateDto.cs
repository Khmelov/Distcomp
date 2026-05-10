namespace Application.Dtos;

public class ReactionUpdateDto
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public string Content { get; set; }
}
