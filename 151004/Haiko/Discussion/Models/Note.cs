using Cassandra.Mapping.Attributes;

namespace Discussion.Models
{
    [Table("tbl_Note")]
    public class Note
    {
        [ClusteringKey]
        public int id { get; set; }
        [ClusteringKey]
        public int tweetId { get; set; }
        public string content { get; set; }
        [PartitionKey]
        public string country { get; set; }
    }
}
