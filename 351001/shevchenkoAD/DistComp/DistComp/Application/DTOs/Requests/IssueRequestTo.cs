using System.ComponentModel.DataAnnotations;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Requests;

public record IssueRequestTo(
    long Id,
    [Required] long AuthorId,
    [Required]
    [StringLength(64, MinimumLength = 2)]
    string Title,
    [Required]
    [StringLength(2048, MinimumLength = 4)]
    string Content
)
    : BaseRequestTo(Id);