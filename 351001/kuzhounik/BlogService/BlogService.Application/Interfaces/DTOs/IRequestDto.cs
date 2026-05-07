namespace BlogService.Application.DependencyInjection.Interfaces.DTOs;

public interface IRequestDto<Key>
{
    Key ID { get; set; }
}