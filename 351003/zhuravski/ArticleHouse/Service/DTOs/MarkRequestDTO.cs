using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.DTOs;

public record MarkRequestDTO
{
    public long? Id {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(32)]
    public string Name {get; init;} = default!;
}