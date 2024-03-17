using System.ComponentModel.DataAnnotations;

namespace TaskREST.Models;

public class Creator
{
    public long Id { get; set; }

    [MaxLength(64)] public string Login { get; set; } = string.Empty;

    [MaxLength(128)] public string Password { get; set; } = string.Empty;

    [MaxLength(64)] public string FirstName { get; set; } = string.Empty;

    [MaxLength(64)] public string LastName { get; set; } = string.Empty;
}