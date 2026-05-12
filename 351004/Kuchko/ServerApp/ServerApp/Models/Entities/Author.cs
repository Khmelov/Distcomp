using System.ComponentModel.DataAnnotations.Schema;

namespace ServerApp.Models.Entities;

[Table("tbl_author")]
public class Author : BaseEntity
{
    [Column("login")] public string Login { get; set; } = null!;

    [Column("password")] public string Password { get; set; } = null!;

    [Column("firstname")] public string Firstname { get; set; } = null!;

    [Column("lastname")] public string Lastname { get; set; } = null!;

    // связь 1 ко многим
    public virtual ICollection<Article> Articles { get; set; } = new List<Article>();
}