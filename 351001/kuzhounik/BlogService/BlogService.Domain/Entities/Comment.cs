using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Shared.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class Comment<Id> : IEntity<Id>
{
    [Key]
    [Column("id")]
    public Id ID { get; set; }
    [Column("story_id")]
    public Id StoryID { get; set; }
    [ForeignKey("StoryID")]
    public Story<Id> Story { get; set; }
    [Column("content")]
    public string Content { get; set; }
    [Column("state")]
    public string State { get; set; } = "PENDING";
}