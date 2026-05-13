using System.ComponentModel.DataAnnotations.Schema;
using Publisher.Domain.Abstractions;

namespace Publisher.Domain.Entities;

[Table("tbl_issue")]
public class Issue : BaseEntity
{
    [Column("author_id")]
    public long AuthorId { get; set; }
    
    [ForeignKey(nameof(AuthorId))]
    public virtual Author Author { get; set; } = null!;

    [Column("title")]
    public string Title { get; set; } = null!;

    [Column("content")]
    public string Content { get; set; } = null!;

    [Column("created")]
    public DateTime Created { get; set; }

    [Column("modified")]
    public DateTime Modified { get; set; }
    
    public virtual ICollection<Label> Labels { get; set; } = new List<Label>();
}