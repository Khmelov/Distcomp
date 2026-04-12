namespace Domain.Models;

public class User
{
    public long Id { get; set; }
    public string? Login { get; set; }
    public string? Password { get; set; }
    public string? Firstname { get; set; }
    public string? Lastname { get; set; }
    
    public List<Topic>? Topics { get; set; }
}
