namespace Domain.Models;

public class Label
{
    public long Id { get; set; }
    public string Name { get; set; } = null!;

    public virtual ICollection<TopicLabel> TopicLabels { get; set; } = new List<TopicLabel>();
}
