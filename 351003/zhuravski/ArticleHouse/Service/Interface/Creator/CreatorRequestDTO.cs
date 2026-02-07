using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.CreatorService;

public record CreatorRequestDTO
{
    [Required]
    public string Login {get; init;} = default!;
    [Required]
    public string Password {get; init;} = default!;
    [Required]
    public string FirstName {get; init;} = default!;
    [Required]
    public string LastName {get; init;} = default!;
};