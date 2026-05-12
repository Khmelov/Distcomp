namespace Shared.Application.Interfaces.DTOs;

public interface IRequestDto<Key>
{
    Key ID { get; set; }
}