using System.ComponentModel.DataAnnotations;
using Application.DTOs.Abstractions;

namespace Application.DTOs.Requests;

public record CommentRequestTo(
    long? Id,
    [Required] long IssueId,
    [Required]
    [StringLength(2048, MinimumLength = 2)]
    string Content
)
    : BaseRequestTo(Id);