using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Shared.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class Sticker<Id> : IEntity<Id>
{
    [Key]
    [Column("id")]
    public Id ID { get; set; }
    [Column("name")]
    public string Text { get; set; }
    public ICollection<Story<Id>> Stories { get; set; } = new List<Story<Id>>();
}