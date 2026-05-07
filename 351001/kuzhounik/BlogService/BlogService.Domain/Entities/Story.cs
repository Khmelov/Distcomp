using BlogService.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class Story : IEntity<long>
{
    public long ID { get; set; }
    public long UserID { get; set; }
    public string Title { get; set; }
    public string Content { get; set; }
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
}