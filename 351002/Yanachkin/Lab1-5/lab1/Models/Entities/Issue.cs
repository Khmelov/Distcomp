namespace lab1.Models.Entities;

public class Issue : BaseEntity
{
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;

    public long EditorId { get; set; }
    public Editor Editor { get; set; } = null!;

    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public ICollection<Label> Labels { get; set; } = new List<Label>();
}
