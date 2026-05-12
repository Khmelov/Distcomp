using System.ComponentModel.DataAnnotations.Schema;

namespace ServerApp.Models.Entities;

[Table("tbl_article")]
public class Article : BaseEntity
{
    [Column("author_id")] public long AuthorId { get; set; }

    [ForeignKey("AuthorId")] public virtual Author Author { get; set; } = null!;

    [Column("title")] public string Title { get; set; } = null!;

    [Column("content")] public string Content { get; set; } = null!;

    [Column("created")] public DateTime Created { get; set; }

    [Column("modified")] public DateTime Modified { get; set; }

    public virtual ICollection<Message> Messages { get; set; } = new List<Message>();
    public virtual ICollection<Sticker> Stickers { get; set; } = new List<Sticker>();
}