using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Publisher.Models;

public class Topic : BaseModel
{
    [Required]
    [MinLength(2)]
    [MaxLength(64)]
    [Column(TypeName = "text")]
    public string Title { get; set; }
    
    [Required]
    [MinLength(4)]
    [MaxLength(2048)]
    [Column(TypeName = "text")]
    public string Content { get; set; }
    
    [Required]
    [DataType(DataType.DateTime)]
    public DateTime Created { get; set; }
    
    [Required]
    [DataType(DataType.DateTime)]
    public DateTime Modified { get; set; }
    
    // Переименовываем user_id в author_id
    [Column("author_id")]
    public long AuthorId { get; set; }  // Новый внешний ключ

    public virtual Author Author { get; set; }  // Навигационное свойство

    public virtual List<Sticker> Stickers { get; set; } = new List<Sticker>();
}