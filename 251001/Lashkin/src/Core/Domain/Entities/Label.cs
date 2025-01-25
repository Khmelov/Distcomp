namespace Domain.Entities;

public class Label : BaseEntity
{
    public string Name { get; set; } = null!;
    
    public virtual ICollection<News> News { get; set; } = new List<News>();
}