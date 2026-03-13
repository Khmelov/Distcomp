using System.ComponentModel.DataAnnotations;
using DistComp.Application.DTOs.Abstractions;

namespace DistComp.Application.DTOs.Requests;

public record CommentRequestTo(
    long Id,
    [Required] long IssueId,
    [Required]
    [StringLength(2048, MinimumLength = 2)]
    string Content
)
    : BaseRequestTo(Id);