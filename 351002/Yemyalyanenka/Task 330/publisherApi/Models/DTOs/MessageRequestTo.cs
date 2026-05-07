namespace RestApiTask.Models.DTOs
{
    public class MessageRequestTo
    {
        public long ArticleId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
