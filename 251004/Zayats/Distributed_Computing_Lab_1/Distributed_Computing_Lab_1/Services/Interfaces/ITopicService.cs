using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;

namespace Distributed_Computing_Lab_1.Services.Interfaces;

public interface ITopicService
{
    Task<IEnumerable<TopicResponseDTO>> GetStoriesAsync();

    Task<TopicResponseDTO> GetStoryByIdAsync(long id);

    Task<TopicResponseDTO> CreateStoryAsync(TopicRequestDTO topic);

    Task<TopicResponseDTO> UpdateStoryAsync(TopicRequestDTO topic);

    Task DeleteStoryAsync(long id);
}