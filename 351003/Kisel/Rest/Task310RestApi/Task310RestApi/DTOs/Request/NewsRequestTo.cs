using System.ComponentModel.DataAnnotations;

namespace Task310RestApi.DTOs.Request
{
    public class NewsRequestTo
    {
        [Required(ErrorMessage = "Creator ID is required")]
        public long CreatorId { get; set; }

        [Required(ErrorMessage = "Title is required")]
        [StringLength(64, MinimumLength = 2, ErrorMessage = "Title must be between 2 and 64 characters")]
        public required string Title { get; set; }

        [Required(ErrorMessage = "Content is required")]
        [StringLength(2048, MinimumLength = 4, ErrorMessage = "Content must be between 4 and 2048 characters")]
        public required string Content { get; set; }

        public List<long> LabelIds { get; set; } = new List<long>();
    }
}