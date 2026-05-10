using Distcomp.Shared.Models;

namespace Distcomp.Domain.Models
{
    public class User
    {
        public long Id { get; set; }
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public UserRole Role { get; set; } = UserRole.CUSTOMER;

        public virtual ICollection<Issue> Issues { get; set; } = new List<Issue>();
    }
}