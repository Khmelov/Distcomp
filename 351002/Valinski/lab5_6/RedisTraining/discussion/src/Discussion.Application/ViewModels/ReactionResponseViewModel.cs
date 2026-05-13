namespace Discussion.Application.ViewModels;

public class ReactionResponseViewModel
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public string Country { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
}
