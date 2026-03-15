using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RV_Kisel_lab2_Task320.Models.Entities;

[Table("tbl_news")]
public class News {
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("title")]
    public string Title { get; set; } = string.Empty;

    [Column("content")]
    public string Content { get; set; } = string.Empty;

    [Column("creator_id")]
    public int CreatorId { get; set; }

    public Creator? Creator { get; set; }
    public ICollection<Label> Labels { get; set; } = new List<Label>();
}