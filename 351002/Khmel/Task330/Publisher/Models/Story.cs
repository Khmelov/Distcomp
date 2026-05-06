using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

[Table("tbl_story", Schema = "distcomp")]
public class Story
{
    [Key]
    [Column("id")] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Id { get; set; }
    
    [Column("writer_id")]
    public long WriterId { get; set; }
    
    [Required]
    [Column("title")]
    public string Title { get; set; } = "";
    
    [Required]
    [Column("content")]
    public string Content { get; set; } = "";
    
    [Column("created")]
    public DateTime Created { get; set; }
    
    [Column("modified")]
    public DateTime Modified { get; set; }
    
    public Writer? Writer { get; set; }
    
    // public ICollection<Comment> Comments { get; set; } = new List<Comment>();
    
    public ICollection<Label> Labels { get; set; } = new List<Label>();
}