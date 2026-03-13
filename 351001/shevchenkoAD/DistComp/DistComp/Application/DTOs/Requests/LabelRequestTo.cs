using System.ComponentModel.DataAnnotations;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Requests;

public record LabelRequestTo(
    long Id,
    [Required]
    [StringLength(32, MinimumLength = 2)]
    string Name
)
    : BaseRequestTo(Id);