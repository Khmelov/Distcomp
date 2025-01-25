namespace Domain.Entities;

public class Notice : BaseEntity
{
    public long NewsId { get; set; }
    public string Content { get; set; } = null!;
    
    public virtual ICollection<News> News { get; set; } = new List<News>();
}