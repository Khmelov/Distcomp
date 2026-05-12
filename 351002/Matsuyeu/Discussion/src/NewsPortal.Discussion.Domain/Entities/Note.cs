namespace Discussion.src.NewsPortal.Discussion.Domain.Entities
{
    public class Note
    {
        public long Id { get; set; }
        public long NewsId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
