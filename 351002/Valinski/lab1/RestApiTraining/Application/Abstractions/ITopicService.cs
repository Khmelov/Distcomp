using Application.Dtos;

namespace Application.Abstractions;

public interface ITopicService
{
    Task<TopicGetDto> CreateTopicAsync(TopicCreateDto topicCreateDto);
    Task<List<TopicGetDto>> GetAllTopicsAsync();
    Task<TopicGetDto> GetTopicByIdAsync(long id);
    Task<TopicGetDto> UpdateTopicAsync(TopicUpdateDto topicUpdateDto);
    Task<bool> DeleteTopicAsync(long id);
}
