namespace Domain.Models;

public class TopicLabel
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public virtual Topic Topic { get; set; } = null!;
    
    public long LabelId { get; set; }
    public virtual Label Label { get; set; } = null!;
}
