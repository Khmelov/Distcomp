using System.ComponentModel.DataAnnotations;

namespace rest1.application.DTOs.requests;

public class RegisterRequestTo
{
    [StringLength(64, MinimumLength = 2)]
    public string Login { get; set; } = string.Empty;

    [StringLength(128, MinimumLength = 8)]
    public string Password { get; set; } = string.Empty;

    [StringLength(64, MinimumLength = 2)]
    public string Firstname { get; set; } = string.Empty;

    [StringLength(64, MinimumLength = 2)]
    public string Lastname { get; set; } = string.Empty;

    public string Role { get; set; } = "CUSTOMER";
}