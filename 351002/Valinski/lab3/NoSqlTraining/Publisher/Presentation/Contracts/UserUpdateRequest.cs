namespace Presentation.Contracts;

public class UserUpdateRequest
{
    public long Id { get; set; } 
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string Firstname { get; set; } = string.Empty;
    public string Lastname { get; set; } = string.Empty;
}
