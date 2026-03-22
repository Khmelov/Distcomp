namespace DataAccess.Models
{
    public class Story : BaseEntity
    {
        public int CreatorId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created {  get; set; }
        public DateTime Modified { get; set; }
    }
}
