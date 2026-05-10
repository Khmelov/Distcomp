namespace ServerApp.Models.DTOs.Responses;

public record ArticleResponseTo(
    long Id, 
    long AuthorId, 
    string Title, 
    string Content, 
    DateTime Created, 
    DateTime Modified
);