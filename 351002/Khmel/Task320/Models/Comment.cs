using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("tbl_comment", Schema = "distcomp")]
public class Comment
{
    [Key]
    [Column("id")] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Id {get; set;}
    [Column("story_id")]
    public long StoryId {get;set;}
    [Required]
    [Column("content")]
    public string Content {get;set;} = "";
    public Story? Story { get; set; }
}
   