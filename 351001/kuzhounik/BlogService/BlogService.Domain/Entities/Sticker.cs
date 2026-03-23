using BlogService.Domain.Interfaces;

namespace BlogService.Domain.Entities;

public class Sticker : IEntity<long>
{
    public long ID { get; set; }
    public string Text { get; set; }
}