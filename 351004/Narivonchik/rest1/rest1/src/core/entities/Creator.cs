namespace rest1.core.entities;

public class Creator(
    string login,
    string password,
    string firstname,
    string lastname) : Entity
{
    public string Login { get; set; } = login;

    public string Password { get; set; } = password;

    public string Firstname { get; set; } = firstname;

    public string Lastname { get; set; } = lastname;
    
    public UserRole Role { get; set; } = UserRole.CUSTOMER;
    
    public IEnumerable<News>? News { get; set; }
}