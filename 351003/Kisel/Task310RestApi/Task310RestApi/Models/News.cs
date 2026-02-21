namespace Task310RestApi.Models
{
    public class News : BaseEntity
    {
        public long CreatorId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; } = DateTime.UtcNow;
        public DateTime Modified { get; set; } = DateTime.UtcNow;
        public List<long> LabelIds { get; set; } = new List<long>();
    }
}