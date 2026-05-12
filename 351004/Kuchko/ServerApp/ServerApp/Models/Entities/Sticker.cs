using System.ComponentModel.DataAnnotations.Schema;

namespace ServerApp.Models.Entities;

[Table("tbl_sticker")]
public class Sticker : BaseEntity
{
    [Column("name")] public string Name { get; set; } = null!;

    public virtual ICollection<Article> Articles { get; set; } = new List<Article>();
}