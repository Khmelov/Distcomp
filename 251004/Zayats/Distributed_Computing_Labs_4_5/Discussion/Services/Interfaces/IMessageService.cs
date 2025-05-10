using Discussion.DTO.Request;
using Discussion.DTO.Response;

namespace Discussion.Services.Interfaces;

public interface IMessageService
{
    Task<IEnumerable<MessageResponseDTO>> GetNoticesAsync();

    Task<MessageResponseDTO> GetNoticeByIdAsync(long id);

    Task<MessageResponseDTO> CreateNoticeAsync(MessageRequestDTO message);

    Task<MessageResponseDTO> UpdateNoticeAsync(MessageRequestDTO message);

    Task DeleteNoticeAsync(long id);
}