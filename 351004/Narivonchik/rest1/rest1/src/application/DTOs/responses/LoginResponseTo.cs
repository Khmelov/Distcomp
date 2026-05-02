namespace rest1.application.DTOs.responses;

public class LoginResponseTo
{
    public string AccessToken { get; set; } = string.Empty;
    public string TokenType { get; set; } = "Bearer";
    public long ExpiresIn { get; set; }
}