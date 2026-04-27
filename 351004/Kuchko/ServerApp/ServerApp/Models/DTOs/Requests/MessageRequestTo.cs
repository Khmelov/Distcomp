using System.ComponentModel.DataAnnotations;

namespace ServerApp.Models.DTOs.Requests;

public record MessageRequestTo(
    [Required] long ArticleId,
    [Required]
    [StringLength(2048, MinimumLength = 4)]
    string Content
);