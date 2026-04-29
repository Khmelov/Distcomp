using Cassandra.Mapping.Attributes;

namespace DataAccess.Models
{
    [Table("posts")]
    public class Post : BaseEntity
    {
        [Column("story_id")]
        public int StoryId { get; set; }

        [Column("content")]
        public string Content { get; set; } = string.Empty;
    }
}
