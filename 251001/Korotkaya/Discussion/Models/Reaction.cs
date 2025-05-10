using Cassandra.Mapping.Attributes;
using System;

namespace Discussion.Models
{
    [Table("tbl_reaction")]
    public class Reaction
    {
        [ClusteringKey(1)]
        [Column("topic_id")]
        public long TopicId { get; set; }

        [ClusteringKey(2)]
        [Column("id")]
        public long Id { get; set; }

        [Column("content")]
        public string Content { get; set; } = string.Empty;

        [Column("created")]
        public DateTime Created { get; set; } = DateTime.UtcNow;

        [Column("modified")]
        public DateTime Modified { get; set; } = DateTime.UtcNow;
    }
}
