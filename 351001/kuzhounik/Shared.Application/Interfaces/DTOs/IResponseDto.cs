namespace Shared.Application.Interfaces.DTOs;

public interface IResponseDto<Key>
{
    Key ID { get; set; }
}