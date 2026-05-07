namespace Domain.Models;

// Comment
public class Story: IIdEntity
{
    public long id { get; set; } = 0;
    public Story issueId { get; set; }
    public string content { get; set; }
}