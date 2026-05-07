namespace ServerApp.Models.DTOs.Responses;

public record MessageResponseTo(
    long Id,
    long ArticleId,
    string Content
);