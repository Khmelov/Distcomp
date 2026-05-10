using System.ComponentModel.DataAnnotations;

namespace rest_api.Dtos
{
    public class ReactionRequestTo
    {
        public long Id { get; set; }
        [Required]
        public long TopicId { get; set; }

        [Required]
        public long UserId { get; set; }

        [Required]
        [StringLength(2048, MinimumLength = 2)]
        public string Content { get; set; } = null!;
    }
}
