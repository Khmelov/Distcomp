namespace Domain.Models;

// Comment
public class Comment: IIdEntity
{
    public long id { get; set; } = 0;
    public long issueId { get; set; }
    public string content { get; set; }
    public Issue issue { get; set; }
}