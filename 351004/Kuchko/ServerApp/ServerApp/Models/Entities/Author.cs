namespace ServerApp.Models.Entities;

public class Author : BaseEntity
{
    public string Login { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string Firstname { get; set; } = null!;
    public string Lastname { get; set; } = null!;
}