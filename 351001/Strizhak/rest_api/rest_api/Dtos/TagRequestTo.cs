using System.ComponentModel.DataAnnotations;

namespace rest_api.Dtos
{
    public class TagRequestTo
    {
        public long Id { get; set; }
        [Required]
        [StringLength(32, MinimumLength = 2)]
        public string Name { get; set; } = null!;
    }
}
