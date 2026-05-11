using Cassandra.Mapping.Attributes;

namespace DiscussionModule.models;

[Table("tbl_notes")]
public class Note
{
    [PartitionKey]
    [Column("id")]
    public long Id { get; set; }

    [Column("news_id")]
    public long NewsId { get; set; }

    [Column("content")]
    public string Content { get; set; } = string.Empty;

    [Column("country")]
    public string Country { get; set; } = string.Empty;
    
    [Column("state")]
    public string State { get; set; } = "PENDING"; // PENDING, APPROVE, DECLINE
}