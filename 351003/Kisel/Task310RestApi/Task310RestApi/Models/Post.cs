namespace Task310RestApi.Models
{
    public class Post : BaseEntity
    {
        public required long NewsId { get; set; }
        public required string Content { get; set; }
        public DateTime Created { get; set; } = DateTime.UtcNow;
        public DateTime Modified { get; set; } = DateTime.UtcNow;
    }
}