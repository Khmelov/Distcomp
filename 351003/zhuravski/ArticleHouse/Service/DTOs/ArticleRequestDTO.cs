using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.DTOs;

public record ArticleRequestDTO
{
    public long? Id {get; init;} = default!;
    public long CreatorId {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(64)]
    public string Title {get; init;} = default!;
    [Required,
    MinLength(4),
    MaxLength(2048)]
    public string Content {get; init;} = default!;
}