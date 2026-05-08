using System.ComponentModel.DataAnnotations;

namespace NewsPortal.Models.Dtos.RequestTo
{
    public class MarkRequestTo
    {
        public long Id { get; set; }

        [Required]
        [StringLength(32, MinimumLength = 2)]
        public string Name { get; set; }
    }
}
