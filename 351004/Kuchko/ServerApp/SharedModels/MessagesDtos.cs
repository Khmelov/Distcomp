using System.ComponentModel.DataAnnotations;

namespace SharedModels;

public record MessageRequestTo(
    [Required] long ArticleId,
    [Required]
    [StringLength(2048, MinimumLength = 2)]
    string Content
);

// Добавили поле State, как требует ТЗ
public record MessageResponseTo(
    long Id,
    long ArticleId,
    string Content,
    string State
);