using Application.Dtos;
using Domain.Models;

namespace Application.Interfaces;

public interface ITopicRepository
{
    Task<List<Topic>> GetAllTopicsAsync();
    Task<Topic?> GetTopicByIdAsync(long id);
    Task<Topic> CreateTopicAsync(TopicCreateDto topicCreateDto);
    Task DeleteTopicAsync (long id);
    Task<Topic> UpdateTopicAsync(TopicUpdateDto topicUpdateDto);
    
}
