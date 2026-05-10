namespace Domain.Models;

public class Reaction
{
    public long Id { get; set; }  
    public long TopicId { get; set; }
    public string? Content { get; set; }
    public Topic? Topic { get; set; }
}
