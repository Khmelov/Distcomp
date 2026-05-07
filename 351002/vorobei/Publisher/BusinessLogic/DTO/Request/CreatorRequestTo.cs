using System.ComponentModel.DataAnnotations;
using DataAccess.Models;

namespace BusinessLogic.DTO.Request
{
    public class CreatorRequestTo : BaseEntity
    {
        [StringLength(64, MinimumLength = 2, ErrorMessage = "Login should be from 2 to 64 symbols")]
        public string Login { get; set; } = string.Empty;

        [StringLength(128, MinimumLength = 8, ErrorMessage = "Password should be from 8 to 128 symbols")]
        public string Password { get; set; } = string.Empty;

        [StringLength(64, MinimumLength = 2, ErrorMessage = "FirstName should be from 2 to 64 symbols")]
        public string FirstName { get; set; } = string.Empty;

        [StringLength(64, MinimumLength = 2, ErrorMessage = "LastName should be from 2 to 64 symbols")]
        public string LastName { get; set; } = string.Empty;
    }
}
