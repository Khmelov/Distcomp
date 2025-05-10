using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;

namespace Publisher.HttpClients.Interfaces;

public interface IDiscussionClient
{
    Task<IEnumerable<MessageResponseDTO>?> GetNoticesAsync();

    Task<MessageResponseDTO?> GetNoticeByIdAsync(long id);

    Task<MessageResponseDTO?> CreateNoticeAsync(MessageRequestDTO post);

    Task<MessageResponseDTO?> UpdateNoticeAsync(MessageRequestDTO post);

    Task DeleteNoticeAsync(long id);
}