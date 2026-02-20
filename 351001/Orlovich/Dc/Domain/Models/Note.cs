namespace Domain.Models;

// Issue
public class Note: IIdEntity
{
    public long id { get; set; }
    public Editor authorId  { get; set; }
    public string title { get; set; }
    public string content { get; set; }
    public DateTime created { get; set; }
    public DateTime modified { get; set; }
}