using System.ComponentModel.DataAnnotations;

namespace ServerApp.Models.DTOs.Requests;

public record ArticleRequestTo(
    [Required] long AuthorId,
    [Required]
    [StringLength(64, MinimumLength = 2)]
    string Title,
    [Required]
    [StringLength(2048, MinimumLength = 4)]
    string Content
);