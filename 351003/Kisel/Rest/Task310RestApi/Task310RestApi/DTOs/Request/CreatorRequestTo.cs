using System.ComponentModel.DataAnnotations;

namespace Task310RestApi.DTOs.Request
{
    public class CreatorRequestTo
    {
        [Required(ErrorMessage = "Login is required")]
        [StringLength(64, MinimumLength = 2, ErrorMessage = "Login must be between 2 and 64 characters")]
        public required string Login { get; set; }

        [Required(ErrorMessage = "Password is required")]
        [StringLength(128, MinimumLength = 8, ErrorMessage = "Password must be between 8 and 128 characters")]
        public required string Password { get; set; }

        [Required(ErrorMessage = "Firstname is required")]
        [StringLength(64, MinimumLength = 2, ErrorMessage = "Firstname must be between 2 and 64 characters")]
        public required string Firstname { get; set; }

        [Required(ErrorMessage = "Lastname is required")]
        [StringLength(64, MinimumLength = 2, ErrorMessage = "Lastname must be between 2 and 64 characters")]
        public required string Lastname { get; set; }
    }
}