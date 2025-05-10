namespace Distributed_Computing_Lab_1.Models;

public class Message : BaseModel
{
    public long TopicId { get; set; }
    public Topic Topic { get; set; }
    
    public string Content { get; set; }
}