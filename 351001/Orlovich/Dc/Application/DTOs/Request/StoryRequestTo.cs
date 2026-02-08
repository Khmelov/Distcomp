namespace Domain.Models;

public class StoryRequestTo
{
    public long EditorId {get; set; }
    public string title { get; set; }
    public string content { get; set; }
    public DateTime created { get; set; }
    public DateTime modified { get; set; }
}