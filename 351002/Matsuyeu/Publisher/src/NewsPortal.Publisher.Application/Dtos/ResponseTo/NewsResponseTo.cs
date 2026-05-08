namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo
{
    // Response DTO - returned to client (no password)
    public class NewsResponseTo
    {
        public long Id { get; set; }
        public long CreatorId { get; set; }
        public string CreatorLogin { get; set; } = string.Empty;
        public string Title { get; set; }
        public string Content { get; set; }
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }
        public List<string>? Marks { get; set; } = new List<string>();
    }
}
