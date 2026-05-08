using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace Redis.Models;

[Table("tbl_label")]
public class Label
{
    [Key]
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    
    [JsonIgnore]
    public List<News> News { get; set; } = new();
}