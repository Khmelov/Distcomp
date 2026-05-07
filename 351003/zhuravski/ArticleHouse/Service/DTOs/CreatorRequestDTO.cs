using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.DTOs;

public record CreatorRequestDTO
{
    public long? Id {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(64)]
    public string Login {get; init;} = default!;
    [Required,
    MinLength(8),
    MaxLength(128)]

    public string Password {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(64)]
    public string FirstName {get; init;} = default!;
    [Required,
    MinLength(2),
    MaxLength(64)]
    public string LastName {get; init;} = default!;
};