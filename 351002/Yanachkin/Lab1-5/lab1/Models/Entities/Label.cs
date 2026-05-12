namespace lab1.Models.Entities;

public class Label : BaseEntity
{
    public string Name { get; set; } = null!;

    public ICollection<Issue> Issues { get; set; } = new List<Issue>();
}
