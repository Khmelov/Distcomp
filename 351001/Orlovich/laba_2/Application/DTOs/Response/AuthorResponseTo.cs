namespace Domain.Models;

public class AuthorResponseTo : IIdEntity
{
    public long id { get; set; }
    public string login { get; set; }
    public string password { get; set; }
    public string firstname { get; set; }
    public string lastname { get; set; }
}