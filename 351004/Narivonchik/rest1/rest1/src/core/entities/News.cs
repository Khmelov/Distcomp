namespace rest1.core.entities;

public class News : Entity
{
    public long CreatorId { get; set; }

    public string Title { get; set; } = string.Empty;

    public string Content { get; set; } = string.Empty;

    public DateTime CreatedAt { get; set; }

    public DateTime Modified { get; set; }

    // lookup object
    public Creator? Creator { get; set; }
    
    public List<Mark> Marks { get; set; } = [];

    public List<Note>? Notes { get; set; }
}