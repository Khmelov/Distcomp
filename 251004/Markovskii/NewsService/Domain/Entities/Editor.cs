namespace Domain.Entities;

public class Editor
{
    public long Id { get; set; }
    public string Login { get; set; }
    public string Password { get; set; }
    public string Firstname { get; set; }
    public string? Lastname { get; set; }
}