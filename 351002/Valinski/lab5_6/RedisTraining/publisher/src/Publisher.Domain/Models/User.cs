namespace Publisher.Domain.Models;

public class User
{
    public long Id { get; set; }
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string? Firstname { get; set; }
    public string? Lastname { get; set; }
    public string Role { get; set; } = string.Empty;
    
    public List<Topic> Topics { get; set; } = new List<Topic>();
}
