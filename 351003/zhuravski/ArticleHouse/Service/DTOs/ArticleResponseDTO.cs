namespace ArticleHouse.Service.DTOs;

public record ArticleResponseDTO
{
    public required long Id {get; init;}
    public required long CreatorId {get; init;}
    public required string Title {get; init;}
    public required string Content {get; init;}
}