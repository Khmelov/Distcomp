namespace CommentMicroservice.Service.DTOs;

public record CommentResponseDTO
{
    public required Guid Id {get; init;}
    public required long ArticleId {get; init;}
    public required string Content {get; init;}
}