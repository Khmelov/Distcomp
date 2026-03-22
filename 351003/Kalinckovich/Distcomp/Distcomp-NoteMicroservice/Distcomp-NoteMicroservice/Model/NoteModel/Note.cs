using System.ComponentModel.DataAnnotations;
using Cassandra.Mapping.Attributes;

namespace Distcomp_NoteMicroservice.Model.NoteModel;

[Table("tbl_note")]
public class Note
{
    [PartitionKey]
    [Column("country")]
    [MaxLength(50)]
    public string Country { get; set; } = string.Empty;
    
    
    [ClusteringKey(0)]
    [Column("topic_id")]
    public long TopicId { get; set; }
    
    [ClusteringKey(1)]
    [Column("id")]
    public long Id { get; set; }
    
    [Column("content")]
    [MinLength(2, ErrorMessage = "Content must be at least 2 characters")]
    [MaxLength(2048, ErrorMessage = "Content cannot exceed 2048 characters")]
    public string Content { get; set; } = string.Empty;
    
    [Column("created_at")]
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;


    [Column("updated_at")]
    public DateTimeOffset? UpdatedAt { get; set; }
}