using System.ComponentModel.DataAnnotations;
using DataAccess.Models;

namespace BusinessLogic.DTO.Request
{
    public class MarkRequestTo : BaseEntity
    {
        [StringLength(32, MinimumLength = 2, ErrorMessage = "Name should be from 2 to 32 symbols")]
        public string Name { get; set; } = string.Empty;
    }
}
