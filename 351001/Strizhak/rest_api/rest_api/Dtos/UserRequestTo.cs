using System.ComponentModel.DataAnnotations;
namespace rest_api.Dtos
{
    public class UserRequestTo
    {
        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Login { get; set; } = null!;

        [Required]
        [StringLength(128, MinimumLength = 8)]
        public string Password { get; set; } = null!;

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Firstname { get; set; } = null!;

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Lastname { get; set; } = null!;
    }
}
