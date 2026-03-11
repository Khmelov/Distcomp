using System.ComponentModel.DataAnnotations;
using DataAccess.Models;

namespace BusinessLogic.DTO.Request
{
    public class StoryRequestTo : BaseEntity
    {
        public int CreatorId { get; set; }

        [StringLength(64, MinimumLength = 2, ErrorMessage = "Title should be from 2 to 64 symbols")]
        public string Title { get; set; } = string.Empty;

        [StringLength(2048, MinimumLength = 4, ErrorMessage = "Content should be from 4 to 2048 symbols")]
        public string Content { get; set; } = string.Empty;
    }
}
