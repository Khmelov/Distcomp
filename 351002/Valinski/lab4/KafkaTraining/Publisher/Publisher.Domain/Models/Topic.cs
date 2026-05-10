namespace Publisher.Domain.Models;

public class Topic
{
    public long Id { get; set; }
    public long UserId { get; set; }
    public virtual User User { get; set; } = null!;
    
    public string Title { get; set; } = null!;
    public string Content { get; set; } = null!;
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }

    public virtual ICollection<TopicLabel> TopicLabels { get; set; } = new List<TopicLabel>();
}
