using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.DTOs;

public record CommentRequestDTO
{
    public long? Id {get; init;} = default!;
    public long ArticleId {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(2048)]
    public string Content {get; init;} = default!;
}