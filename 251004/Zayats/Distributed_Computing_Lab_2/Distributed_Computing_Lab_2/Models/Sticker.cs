using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Distributed_Computing_Lab_2.Models;

public class Sticker : BaseModel
{
    [Required]
    [MinLength(2)]
    [MaxLength(32)]
    [Column("name", TypeName = "text")]  // Указываем, что столбец называется "Name" с большой буквы
    public string Name { get; set; }

    public virtual List<Topic> Stories { get; set; } = [];
}
