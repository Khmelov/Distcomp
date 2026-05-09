using System.ComponentModel.DataAnnotations;

namespace rest_api.Dtos
{
    public class TopicRequestTo
    {
        [Required]
        public long Id { get; set; }
        [Required]
        public long UserId { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Title { get; set; } = null!;

        [Required]
        [StringLength(2048, MinimumLength = 4)]
        public string Content { get; set; } = null!;
        public List<string>? Tags { get; set; }
    }
}
