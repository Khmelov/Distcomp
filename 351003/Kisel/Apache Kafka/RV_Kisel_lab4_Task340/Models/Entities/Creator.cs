using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RV_Kisel_lab2_Task320.Models.Entities;

[Table("tbl_creator")]
public class Creator {
    [Key]
    [Column("id")] // <--- ДОБАВИТЬ ЭТО!
    public int Id { get; set; }

    [Column("login")] // <--- И для остальных колонок тоже!
    public string Login { get; set; } = string.Empty;

    [Column("password")]
    public string Password { get; set; } = string.Empty;

    [Column("firstname")]
    public string Firstname { get; set; } = string.Empty;

    [Column("lastname")]
    public string Lastname { get; set; } = string.Empty;

    public ICollection<News> News { get; set; } = new List<News>();
}