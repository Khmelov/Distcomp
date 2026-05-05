using System.ComponentModel.DataAnnotations.Schema;
using Domain.Abstractions;

namespace Domain.Entities;

[Table("tbl_author")]
public class Author : BaseEntity
{
    [Column("login")]
    public string Login { get; set; } = null!;

    [Column("password")]
    public string Password { get; set; } = null!;

    [Column("firstname")]
    public string Firstname { get; set; } = null!;

    [Column("lastname")]
    public string Lastname { get; set; } = null!;
    
    public virtual ICollection<Issue> Issues { get; set; } = new List<Issue>();
}
