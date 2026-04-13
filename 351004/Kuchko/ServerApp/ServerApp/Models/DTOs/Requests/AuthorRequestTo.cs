using System.ComponentModel.DataAnnotations;

namespace ServerApp.Models.DTOs.Requests;

public record AuthorRequestTo(
    [Required]
    [StringLength(64, MinimumLength = 2)]
    string Login,
    [Required]
    [StringLength(128, MinimumLength = 8)]
    string Password,
    [Required]
    [StringLength(64, MinimumLength = 2)]
    string Firstname,
    [Required]
    [StringLength(64, MinimumLength = 2)]
    string Lastname
);