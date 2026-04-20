namespace DiscussionModule.DTOs.responses;

public class NewsResponseTo(
    string title, 
    string content, 
    DateTime createdAt, 
    DateTime modified)
{
    public long Id { get; set; }

    public long CreatorId { get; set; }

    public string Title { get; set; } = title;

    public string Content { get; set; } = content;

    public DateTime CreatedAt { get; set; } = createdAt;

    public DateTime Modified { get; set; } = modified;
}