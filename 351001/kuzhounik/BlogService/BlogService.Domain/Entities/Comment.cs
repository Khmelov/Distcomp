using BlogService.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class Comment : IEntity<long>
{
    public long ID { get; set; }
    public long StoryID { get; set; }
    public string Content { get; set; }
}