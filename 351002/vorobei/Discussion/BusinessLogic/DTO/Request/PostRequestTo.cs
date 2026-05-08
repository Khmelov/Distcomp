using System.ComponentModel.DataAnnotations;
using DataAccess.Models;

namespace BusinessLogic.DTO.Request
{
    public class PostRequestTo : BaseEntity
    {
        public int StoryId { get; set; }

        [StringLength(2048, MinimumLength = 2, ErrorMessage = "Content should be from 2 to 2048 symbols")]
        public string Content { get; set; } = string.Empty;

        public PostState State { get; set; } = PostState.PENDING;
    }
}
