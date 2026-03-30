namespace Domain.Models;

public class Topic
{
    public long Id { get; set; }  
    public long UserId { get; set; }
    public string? Title { get; set; }
    public string? Content { get; set; }
    public DateTime CreatedAt { get; set; }
    public DateTime ModifiedAt { get; set; }
    
    public User? User { get; set; }
    public List<Label>? Labels { get; set; }
    public List<Reaction>? Reactions { get; set; }
}
