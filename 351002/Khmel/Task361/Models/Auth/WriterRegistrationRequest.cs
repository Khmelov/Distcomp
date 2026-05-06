using System.ComponentModel.DataAnnotations;

public class WriterRegistrationRequest
{
    [StringLength(64, MinimumLength = 2)]
    public string Login { get; set; } = "";

    [StringLength(128, MinimumLength = 8)]
    public string Password { get; set; } = "";

    [StringLength(64, MinimumLength = 2)]
    public string Firstname { get; set; } = "";

    [StringLength(64, MinimumLength = 2)]
    public string Lastname { get; set; } = "";

    public string? Role { get; set; }
}