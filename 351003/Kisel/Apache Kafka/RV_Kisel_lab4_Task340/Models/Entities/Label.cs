using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RV_Kisel_lab2_Task320.Models.Entities;

[Table("tbl_label")]
public class Label {
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("name")]
    public string Name { get; set; } = string.Empty;

    public ICollection<News> News { get; set; } = new List<News>();
}