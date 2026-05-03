using Cassandra.Mapping.Attributes;

namespace DataAccess.Models
{
    [Table("posts_by_id")]
    public class PostById
    {
        [PartitionKey]
        [Column("post_id")]
        public int Id { get; set; }

        [Column("story_id")]
        public int StoryId { get; set; }

        [Column("content")]
        public string Content { get; set; } = string.Empty;
    }

}
