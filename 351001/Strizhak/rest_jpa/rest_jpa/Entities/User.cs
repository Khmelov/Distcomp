using rest_api.Entities;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace rest_api
{
    public class User: IEntity
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public long Id { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Login { get; set; } = null!;
        [Required]
        [StringLength(128, MinimumLength = 2)]
        public string Password { get; set; } = null!;
        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Firstname { get; set; } = null!;
        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Lastname { get; set; } = null!;

        // Навигационные свойства для связей
        public ICollection<Topic> Topics { get; set; } = new List<Topic>();
        public ICollection<Reaction> Reactions { get; set; } = new List<Reaction>();
    }
}
