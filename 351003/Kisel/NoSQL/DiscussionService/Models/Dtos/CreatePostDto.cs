using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Models.Dtos
{
    // Этот DTO мы будем принимать от клиента при создании поста
    public class CreatePostDto
    {
        [Required]
        public int NewsId { get; set; }

        [Required]
        [StringLength(2048, MinimumLength = 2)]
        public string Content { get; set; } = string.Empty;
    }
}