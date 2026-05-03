using Publisher.Entities;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Publisher.Entities
{
    public class Tag: IEntity
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long Id { get; set; }
        [Required]
        [StringLength(32, MinimumLength = 2)]
        public string Name { get; set; } = null!;
        public ICollection<TopicTag> TopicTags { get; set; } = new List<TopicTag>();
    }
}
