using System.ComponentModel.DataAnnotations.Schema;

namespace DataAccess.Models
{
    public class Creator : BaseEntity
    {
        [Column("login")]
        public string Login { get; set; } = string.Empty;
        [Column("password")]
        public string Password { get; set; } = string.Empty;
        [Column("firstname")]
        public string Firstname { get; set; } = string.Empty;
        [Column("lastname")]
        public string Lastname { get; set; } = string.Empty;
        public ICollection<Story> Stories { get; set; }
    }
}
