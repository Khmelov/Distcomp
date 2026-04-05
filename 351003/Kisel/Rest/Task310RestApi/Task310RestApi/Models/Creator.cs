
namespace Task310RestApi.Models
{
    public class Creator : BaseEntity
    {
        public required string Login { get; set; }
        public required string Password { get; set; }
        public required string Firstname { get; set; }
        public required string Lastname { get; set; }
        public DateTime Created { get; set; } = DateTime.UtcNow;
        public DateTime Modified { get; set; } = DateTime.UtcNow;
    }
}
