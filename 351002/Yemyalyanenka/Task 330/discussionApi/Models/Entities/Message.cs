using Cassandra.Mapping.Attributes;
namespace RestApiTask.Models.Entities;

[Table("tbl_message")]
public class Message : IHasId
{
    [PartitionKey]
    [Column("id")]
    public long Id { get; set; }

    [Column("article_id")]
    public long ArticleId { get; set; }

    [Column("content")]
    public string Content { get; set; } = string.Empty;

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}