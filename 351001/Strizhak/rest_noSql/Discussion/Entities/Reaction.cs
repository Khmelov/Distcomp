using Cassandra.Mapping.Attributes;
namespace Discussion.Entities
{
    [Table("tbl_reaction")]
    public class Reaction
    {
        [PartitionKey]
        [Column("topic_id")]
        public long TopicId { get; set; }

        [ClusteringKey(0)]
        [Column("id")]
        public long Id { get; set; }

        [Column("content")]
        public string Content { get; set; }
    }
}
