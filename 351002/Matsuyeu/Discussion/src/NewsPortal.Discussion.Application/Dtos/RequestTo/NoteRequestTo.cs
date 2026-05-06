using System.ComponentModel.DataAnnotations;

namespace Discussion.src.NewsPortal.Discussion.Application.Dtos.RequestTo
{
    public class NoteRequestTo
    {
        public long Id { get; set; }
        public long NewsId { get; set; }

        [Required]
        [StringLength(2048, MinimumLength = 2)]
        public string Content { get; set; }
    }
}
