using BlogService.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class User : IEntity<long>
{
    public long ID { get; set; }
    public string Login { get; set; }
    public string Password { get; set; }
    
    public string FirstName { get; set; }
    public string LastName { get; set; }
}