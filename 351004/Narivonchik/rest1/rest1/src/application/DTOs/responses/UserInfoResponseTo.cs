namespace rest1.application.DTOs.responses;

public class UserInfoResponseTo
{
    public long Id { get; set; }
    public string Login { get; set; } = string.Empty;
    public string Firstname { get; set; } = string.Empty;
    public string Lastname { get; set; } = string.Empty;
    public string Role { get; set; } = string.Empty;
}