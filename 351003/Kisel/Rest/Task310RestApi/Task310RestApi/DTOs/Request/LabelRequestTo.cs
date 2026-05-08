using System.ComponentModel.DataAnnotations;

namespace Task310RestApi.DTOs.Request
{
    public class LabelRequestTo
    {
        [Required(ErrorMessage = "Name is required")]
        [StringLength(32, MinimumLength = 2, ErrorMessage = "Name must be between 2 and 32 characters")]
        public required string Name { get; set; }
    }
}