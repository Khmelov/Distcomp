namespace Domain.Models;

public class User
{
    public long Id { get; set; }
    public string Login { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string Firstname { get; set; } = null!;
    public string Lastname { get; set; } = null!;

    public virtual ICollection<Topic> Topics { get; set; } = new List<Topic>();
}
