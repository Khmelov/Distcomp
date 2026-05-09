using Publisher.Entities;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Publisher.Entities
{
    public class Topic: IEntity
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long Id { get; set; }

        [Required]
        public long UserId { get; set; } 

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Title { get; set; } = null!;

        [Required]
        [StringLength(2048, MinimumLength = 4)]
        public string Content { get; set; } = null!;

        [Required]
        public DateTime Created { get; set; } 

        [Required]
        public DateTime Modified { get; set; }

        // Навигация
        public User User { get; set; } = null!;
        public ICollection<TopicTag> TopicTags { get; set; } = new List<TopicTag>();
    }
}
