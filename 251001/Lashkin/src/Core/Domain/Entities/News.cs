namespace Domain.Entities;

public class News : BaseEntity
{
    public long UserId { get; set; }
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
    
    public virtual User User { get; set; } = null!;
    public virtual Notice Notice { get; set; } = null!;
    public virtual ICollection<Label> Labels { get; set; } = new List<Label>();
}