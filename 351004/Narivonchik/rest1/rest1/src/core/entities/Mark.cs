namespace rest1.core.entities;

public class Mark(string name) : Entity
{
    public string Name { get; set; } = name;
    public List<News> News { get; set; } = [];
}