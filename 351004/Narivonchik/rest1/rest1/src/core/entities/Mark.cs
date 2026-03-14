namespace rest1.core.entities;

public class Mark(string name) : Entity
{
    public string Name { get; set; } = name;
    public long[] NewsIds { get; set; } = new long[0];
}