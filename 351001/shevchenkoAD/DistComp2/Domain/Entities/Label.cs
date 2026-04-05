using System.ComponentModel.DataAnnotations.Schema;
using Domain.Abstractions;

namespace Domain.Entities;

[Table("tbl_label")]
public class Label : BaseEntity
{
    [Column("name")]
    public string Name { get; set; } = null!;
    
    public virtual ICollection<Issue> Issues { get; set; } = new List<Issue>();
}