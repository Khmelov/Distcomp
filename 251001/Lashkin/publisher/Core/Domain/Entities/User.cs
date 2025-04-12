namespace Domain.Entities;

public class User : BaseEntity
{
    public string Login { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string FirstName { get; set; } = null!;
    public string LastName { get; set; } = null!;

    public virtual ICollection<News> News { get; set; } = new List<News>();
}