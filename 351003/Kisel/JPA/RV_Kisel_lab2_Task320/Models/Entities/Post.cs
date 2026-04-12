using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RV_Kisel_lab2_Task320.Models.Entities;

[Table("tbl_post")]
public class Post {
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("content")]
    public string Content { get; set; } = string.Empty;

    [Column("news_id")]
    public int NewsId { get; set; }

    public News? News { get; set; }
}