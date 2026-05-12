namespace Publisher.src.NewsPortal.Publisher.Domain.Entities
{
    public class Note
    {
        public long Id { get; set; }
        public long NewsId { get; set; }
        public string Content { get; set; } = string.Empty;
        public string State { get; set; } = "PENDING";
    }
}
