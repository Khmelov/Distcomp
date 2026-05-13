using System.ComponentModel.DataAnnotations;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo
{
    public class MarkRequestTo
    {
        public long Id { get; set; }

        [Required]
        [StringLength(32, MinimumLength = 2)]
        public string Name { get; set; }
    }
}
