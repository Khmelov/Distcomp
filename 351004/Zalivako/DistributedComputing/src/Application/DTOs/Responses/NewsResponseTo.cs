namespace Application.DTOs.Responses
{
    public class NewsResponseTo(
        string title, 
        string content, 
        DateTime createdAt, 
        DateTime modified)
    {
        public long Id { get; set; }

        public long EditorId { get; set; }

        public string Title { get; set; } = title;

        public string Content { get; set; } = content;

        public DateTime CreatedAt { get; set; } = createdAt;

        public DateTime Modified { get; set; } = modified;
    }
}
