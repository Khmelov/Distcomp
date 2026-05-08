using System.ComponentModel.DataAnnotations;

namespace NewsPortal.Models.Dtos.RequestTo
{
    public class NewsRequestTo
    {
        public long Id { get; set; }

        [Required]
        public long CreatorId { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Title { get; set; }

        [Required]
        [StringLength(2048, MinimumLength = 4)]
        public string Content { get; set; }

        public List<string>? Marks { get; set; }
    }
}
