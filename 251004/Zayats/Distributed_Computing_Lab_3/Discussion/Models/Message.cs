using Cassandra.Mapping.Attributes;

namespace Discussion.Models;

[Table("tbl_message")]
public class Message : BaseModel
{
    [ClusteringKey]
    public long Id { get; set; } 
    public long TopicId { get; set; }
    public string Content { get; set; }
}