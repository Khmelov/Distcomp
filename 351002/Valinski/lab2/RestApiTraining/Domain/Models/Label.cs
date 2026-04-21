namespace Domain.Models;

public class Label
{
    public long Id { get; set; }    
    public string? Name { get; set; }
    
    public List<Topic>? Topics { get; set; }
}
