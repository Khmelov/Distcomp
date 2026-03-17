
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace rest_api.Entities
{
    public class Reaction: IEntity
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long Id { get; set; }
        [Required]
        public long TopicId { get; set; }
        [Required]
        [StringLength(2048, MinimumLength = 2)]
        public string Content { get; set; } = null!;
        // Навигационное свойство для связи с Topic
        public Topic Topic { get; set; }
    }
}
