namespace Domain.Models;

public class IssueResponseTo : IIdEntity
{
    public long id { get; set; }
    public long authorId  { get; set; }
    public string title { get; set; }
    public string content { get; set; }
    public DateTime created { get; set; }
    public DateTime modified { get; set; }
}