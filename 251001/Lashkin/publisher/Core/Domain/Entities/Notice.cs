namespace Domain.Entities;

public class Notice : BaseEntity
{
    public long NewsId { get; set; }
    public string Content { get; set; } = null!;
    
    public virtual News News { get; set; }
}