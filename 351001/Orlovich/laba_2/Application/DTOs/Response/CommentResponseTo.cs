namespace Domain.Models;

public class CommentResponseTo : IIdEntity
{
    public long id { get; set; }
    public long issueId { get; set; }
    public string content { get; set; }
}