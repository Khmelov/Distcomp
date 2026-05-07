namespace BlogService.Application.DependencyInjection.Interfaces.DTOs;

public interface IResponseDto<Key>
{
    Key ID { get; set; }
}