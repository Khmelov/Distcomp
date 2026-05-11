namespace lab1.Models.DTO.Auth;

public sealed class LoginRequest
{
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}
