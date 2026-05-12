using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Shared.Domain.Interfaces;

namespace BlogService.Domain.Entities;

[Table("tbl_story")]
public class Story<Id> : IEntity<Id>
{
    [Key]
    [Column("id")]
    public Id ID { get; set; }
    [Column("user_id")]
    public Id UserID { get; set; }
    [ForeignKey("UserID")]
    public User<Id> User { get; set; }
    [Column("title")]    
    public string Title { get; set; }
    [Column("content")]
    public string Content { get; set; }
    [Column("created")]
    public DateTime Created { get; set; }
    [Column("modified")]
    public DateTime Modified { get; set; }
    
    public ICollection<Comment<Id>> Comments { get; set; } = new List<Comment<Id>>();
    public ICollection<Sticker<Id>> Stickers { get; set; } = new List<Sticker<Id>>();
}