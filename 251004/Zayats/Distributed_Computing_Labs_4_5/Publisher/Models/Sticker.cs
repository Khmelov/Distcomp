using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Publisher.Models;

public class Sticker : BaseModel
{
    [Required]
    [MinLength(2)]
    [MaxLength(32)]
    [Column("name", TypeName = "text")]
    public string Name { get; set; }

    public virtual List<Topic> Topics { get; set; } = [];
}