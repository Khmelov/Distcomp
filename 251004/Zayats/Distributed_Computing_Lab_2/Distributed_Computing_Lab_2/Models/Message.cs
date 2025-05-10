using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Distributed_Computing_Lab_2.Models;

public class Message : BaseModel
{
    [Required]
    [MinLength(2)]
    [MaxLength(2048)]
    [Column(TypeName = "text")]
    public string Content { get; set; }
    
    public long TopicId { get; set; }
    public virtual Topic Topic { get; set; }
}