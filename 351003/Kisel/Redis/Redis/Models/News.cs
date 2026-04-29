using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Redis.Models;

[Table("tbl_news")]
public class News
{
    [Key]
    public int Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
    
    public int CreatorId { get; set; }
    public Creator? Creator { get; set; }
    
    public List<Label> Labels { get; set; } = new();
}