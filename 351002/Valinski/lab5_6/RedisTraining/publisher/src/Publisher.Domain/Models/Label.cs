namespace Publisher.Domain.Models;

public class Label
{
    public long Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public List<Topic> Topics { get; set; } = new List<Topic>();
}
