using System.ComponentModel.DataAnnotations;

namespace ArticleHouse.Service.DTOs;

public record LoginRequestDTO
{
    [Required]
    public string Login {get; init;} = default!;
    [Required]
    public string Password {get; init;} = default!;
}