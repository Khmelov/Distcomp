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

    [Column("country")] // Добавлено согласно схеме
    public string Country { get; set; } = "USA";

    [Column("content")]
    public string Content { get; set; } = string.Empty;
}