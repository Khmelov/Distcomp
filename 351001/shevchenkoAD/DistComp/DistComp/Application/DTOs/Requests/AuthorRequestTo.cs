using System.ComponentModel.DataAnnotations;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Requests;

public record AuthorRequestTo(
    long Id,
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
)
    : BaseRequestTo(Id);