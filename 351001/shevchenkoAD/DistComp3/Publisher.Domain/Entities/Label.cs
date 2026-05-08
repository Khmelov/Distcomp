using System.ComponentModel.DataAnnotations.Schema;
using Publisher.Domain.Abstractions;

namespace Publisher.Domain.Entities;

[Table("tbl_label")]
public class Label : BaseEntity
{
    [Column("name")]
    public string Name { get; set; } = null!;
    
    public virtual ICollection<Issue> Issues { get; set; } = new List<Issue>();
}