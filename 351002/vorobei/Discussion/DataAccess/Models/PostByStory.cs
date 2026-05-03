using Cassandra.Mapping.Attributes;

namespace DataAccess.Models
{
    [Table("posts_by_story")]
    public class PostByStory
    {
        [PartitionKey(0)]
        [Column("story_id")]
        public int StoryId { get; set; }

        [PartitionKey(1)]
        [Column("bucket")]
        public int Bucket { get; set; }

        [ClusteringKey(0)]
        [Column("post_id")]
        public int Id { get; set; }

        [Column("content")]
        public string Content { get; set; } = string.Empty;
    }
}
