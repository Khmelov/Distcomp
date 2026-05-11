using System.ComponentModel.DataAnnotations;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo
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
