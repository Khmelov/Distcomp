using Cassandra.Mapping.Attributes;

namespace Discussion.Models
{
    public enum CommentState
    {
        PENDING,
        APPROVE,
        DECLINE
    }

    [Table("tbl_comment", Keyspace = "distcomp")]
    public class Comment
    {
        [PartitionKey]
        [Column("story_id")]
        public long StoryId { get; set; }

        [ClusteringKey(0)]
        [Column("id")]
        public long Id { get; set; }

        [Column("content")]
        public string Content { get; set; } = string.Empty;

        [Column("country")]
        public string Country { get; set; } = string.Empty;

        [Column("created")]
        public DateTimeOffset Created { get; set; }

        [Column("state")]
        public string State { get; set; } = "PENDING";
    }
}