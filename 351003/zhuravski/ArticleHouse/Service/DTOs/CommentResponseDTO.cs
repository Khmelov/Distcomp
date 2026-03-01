namespace ArticleHouse.Service.DTOs;

public record CommentResponseDTO
{
    public required long Id {get; init;}
    public required long ArticleId {get; init;}
    public required string Content {get; init;}
}