namespace lab1.Models.Entities;

public class Editor : BaseEntity
{
    public string Login { get; set; } = null!;
    public string Password { get; set; } = null!;
    public string FirstName { get; set; } = null!;
    public string LastName { get; set; } = null!;

    public ICollection<Issue> Issues { get; set; } = new List<Issue>();
}
