using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("tbl_label", Schema = "distcomp")]
public class Label
{
    [Key]
    [Column("id")] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Id {get; set;}
    [Required]
    [Column("name")]
    public string Name {get; set;} = "";
    public ICollection<Story> Stories { get; set; } = new List<Story>();
}