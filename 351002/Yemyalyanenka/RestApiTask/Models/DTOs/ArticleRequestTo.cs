namespace RestApiTask.Models.DTOs
{
    public record ArticleRequestTo(long WriterId, string Title, string Content);
}
