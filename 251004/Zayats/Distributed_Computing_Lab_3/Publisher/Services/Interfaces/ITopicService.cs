using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;

namespace Publisher.Services.Interfaces;

public interface ITopicService
{
    Task<IEnumerable<TopicResponseDTO>> GetStoriesAsync();

    Task<TopicResponseDTO> GetStoryByIdAsync(long id);

    Task<TopicResponseDTO> CreateStoryAsync(TopicRequestDTO topic);

    Task<TopicResponseDTO> UpdateStoryAsync(TopicRequestDTO topic);

    Task DeleteStoryAsync(long id);
}