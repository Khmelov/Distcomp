using System.ComponentModel.DataAnnotations;

namespace rest1.application.DTOs.requests;

public class LoginRequestTo
{
    [Required]
    public string Login { get; set; } = string.Empty;

    [Required]
    public string Password { get; set; } = string.Empty;
}