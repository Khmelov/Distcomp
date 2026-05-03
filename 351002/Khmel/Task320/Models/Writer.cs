using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("tbl_writer", Schema = "distcomp")]
public class Writer
{
    [Key]
    [Column("id")] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Id {get; set;}

    [Required]
    [Column("login")]
    public string Login {get; set;} = "anna.hmel.6@gmail.com";

    [Required]
    [Column("password")]
    public string Password {get; set;} = "";

    [Required]
    [Column("firstname")]
    public string Firstname {get; set;} = "Анна";

    [Required]
    [Column("lastname")]
    public string Lastname {get; set;} = "Хмель";
    public ICollection<Story> Stories { get; set; } = new List<Story>();
}