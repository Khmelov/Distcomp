namespace DataAccess.Models
{
    public class Post : BaseEntity
    {
        public int StoryId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
