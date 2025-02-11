namespace Domain.Entities;

public class News
{
    public long Id { get; set; }
    public long EditorId { get; set; }
    public string Title { get; set; }
    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
}