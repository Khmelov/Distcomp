namespace ArticleHouse.Service.DTOs;

public record MarkResponseDTO
{
    public required long Id {get; init;}
    public required string Name {get; init;}
}