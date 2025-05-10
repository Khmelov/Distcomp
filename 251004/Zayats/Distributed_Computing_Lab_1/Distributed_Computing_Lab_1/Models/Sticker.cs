namespace Distributed_Computing_Lab_1.Models;

public class Sticker : BaseModel
{
    public string Name { get; set; }

    public List<Topic> Stories { get; set; } = [];
}