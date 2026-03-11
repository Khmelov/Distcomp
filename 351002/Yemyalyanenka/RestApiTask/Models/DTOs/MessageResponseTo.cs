namespace RestApiTask.Models.DTOs
{
    public class MessageResponseTo
    {
        public long Id { get; set; }
        public long ArticleId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
