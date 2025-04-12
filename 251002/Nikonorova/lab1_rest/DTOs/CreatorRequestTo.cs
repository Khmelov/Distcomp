using System.ComponentModel.DataAnnotations;

namespace distcomp.DTOs
{
    public class CreatorRequestTo
    {
        public long Id { get; set; } 

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Login { get; set; } 

        [Required]
        [StringLength(128, MinimumLength = 8)]
        public string Password { get; set; } 

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Firstname { get; set; } 

        [Required]
        [StringLength(64, MinimumLength = 2)]
        public string Lastname { get; set; } 
    }
}
