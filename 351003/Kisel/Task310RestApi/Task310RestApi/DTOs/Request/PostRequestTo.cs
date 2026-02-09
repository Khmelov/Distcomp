using System.ComponentModel.DataAnnotations;

namespace Task310RestApi.DTOs.Request
{
    public class PostRequestTo
    {
        [Required(ErrorMessage = "News ID is required")]
        public long NewsId { get; set; }

        [Required(ErrorMessage = "Content is required")]
        [StringLength(2048, MinimumLength = 4, ErrorMessage = "Content must be between 4 and 2048 characters")]
        public required string Content { get; set; }
    }
}