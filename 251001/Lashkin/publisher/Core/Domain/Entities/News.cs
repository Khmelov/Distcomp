namespace Domain.Entities;

public class News : BaseEntity
{
    public long UserId { get; set; }
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public virtual User User { get; set; }
    public virtual ICollection<Notice> Notices { get; set; } = new List<Notice>();
    public virtual ICollection<Label> Labels { get; set; } = new List<Label>();
}