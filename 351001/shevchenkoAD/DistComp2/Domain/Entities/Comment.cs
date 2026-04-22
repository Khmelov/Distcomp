using System.ComponentModel.DataAnnotations.Schema;
using Domain.Abstractions;

namespace Domain.Entities;

[Table("tbl_comment")]
public class Comment : BaseEntity
{
    [Column("issue_id")]
    public long IssueId { get; set; }
    
    [ForeignKey(nameof(IssueId))]
    public virtual Issue Issue { get; set; } = null!;

    [Column("content")]
    public string Content { get; set; } = null!;
}