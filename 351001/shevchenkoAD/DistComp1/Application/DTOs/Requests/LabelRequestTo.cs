using System.ComponentModel.DataAnnotations;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Requests;

public record LabelRequestTo(
    long Id,
    [Required]
    [StringLength(32, MinimumLength = 2)]
    string Name
)
    : BaseRequestTo(Id);