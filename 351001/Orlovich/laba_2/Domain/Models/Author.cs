namespace Domain.Models;

// Author
public class Author : IIdEntity
{
    public long id { get; set; } = 0;
    public string login { get; set; }
    public string password { get; set; }
    public string firstname { get; set; }
    public string lastname { get; set; }

    public ICollection<Issue> issues { get; set; }
}