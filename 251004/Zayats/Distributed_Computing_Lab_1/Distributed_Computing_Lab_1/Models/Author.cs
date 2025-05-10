namespace Distributed_Computing_Lab_1.Models;

public class Author : BaseModel
{
    public string Login { get; set; }
    public string Password { get; set; }
    public string Firstname { get; set; }
    public string Lastname { get; set; }

    public List<Topic> Stories { get; set; } = [];
}