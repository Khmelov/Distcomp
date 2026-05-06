namespace RestApiTask.Models.DTOs
{
    public class ArticleResponseTo
    {
        public long Id { get; set; }
        public long WriterId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }
    }
}
