using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.DTO.ResponseDTO;

namespace Distributed_Computing_Lab_2.Services.Interfaces;

public interface IMessageService
{
    Task<IEnumerable<MessageResponseDTO>> GetNoticesAsync();

    Task<MessageResponseDTO> GetNoticeByIdAsync(long id);

    Task<MessageResponseDTO> CreateNoticeAsync(MessageRequestDTO message);

    Task<MessageResponseDTO> UpdateNoticeAsync(MessageRequestDTO message);

    Task DeleteNoticeAsync(long id);
}