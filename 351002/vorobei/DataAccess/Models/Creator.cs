using System.ComponentModel.DataAnnotations;

namespace DataAccess.Models
{
    public class Creator : BaseEntity
    {
        public string Login { get; set; } = string.Empty;

        public string Password { get; set; } = string.Empty;

        public string Firstname { get; set; } = string.Empty;

        public string Lastname { get; set; } = string.Empty;
    }
}
