using Cassandra.Mapping.Attributes;

namespace Discussion.Models
{
    [Table("tbl_comment", Keyspace = "distcomp")]
    public class Comment
    {
        [PartitionKey]
        [Column("id")]
        public long Id { get; set; }

        [Column("story_id")]
        public long StoryId { get; set; }

        [Column("content")]
        public string Content { get; set; } = "";
    }
}