using System.ComponentModel.DataAnnotations.Schema;

namespace ServerApp.Models.Entities;

[Table("tbl_message")]
public class Message : BaseEntity
{
    [Column("article_id")] public long ArticleId { get; set; }

    [ForeignKey("ArticleId")] // Указываем связь по внешнему ключу
    public virtual Article Article { get; set; } = null!;

    [Column("content")] public string Content { get; set; } = null!;
}