using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Publisher.Models;

public class Sticker : BaseModel
{
    [Required]
    [MinLength(2)]
    [MaxLength(32)]
    [Column(TypeName = "text")]
    public string name { get; set; }

    public virtual List<Topic> Stories { get; set; } = [];
}