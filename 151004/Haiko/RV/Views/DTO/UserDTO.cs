using System.ComponentModel.DataAnnotations;

namespace RV.Views.DTO
{
    public class UserDTO
    {
        [Required]
        public int id { get; set; }
        [Required]
        public string login { get; set; }
        [Required]
        public string password { get; set; }
        [Required]
        public string firstname { get; set; }
        [Required]
        public string lastname { get; set; }
    }
}
