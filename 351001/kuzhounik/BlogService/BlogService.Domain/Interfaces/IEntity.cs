namespace BlogService.Domain.Interfaces;

public interface IEntity<Key>
{
    Key ID { get; set; }
}