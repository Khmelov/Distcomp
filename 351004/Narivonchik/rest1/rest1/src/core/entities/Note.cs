namespace rest1.core.entities;

public class Note : Entity
{
    public long NewsId { get; set; }

    public string Content { get; set; } = string.Empty;

    public string Country { get; set; } = string.Empty;
    
    // lookup object
    public News? News { get; set; }
   
}