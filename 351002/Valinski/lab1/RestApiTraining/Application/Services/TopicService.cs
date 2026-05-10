using Application.Abstractions;
using Application.Dtos;
using Application.Interfaces;
using AutoMapper;

namespace Application.Services;

public class TopicService : ITopicService
{
    private readonly ITopicRepository _topicRepository;
    private readonly IMapper _mapper;
    
    public TopicService(ITopicRepository topicRepository, IMapper mapper)
    {
        _topicRepository = topicRepository;
        _mapper = mapper;
    }

    public async Task<TopicGetDto> CreateTopicAsync(TopicCreateDto topicCreateDto)
    {
        var topic = await _topicRepository.CreateTopicAsync(topicCreateDto);
        return _mapper.Map<TopicGetDto>(topic);
    }

    public async Task<List<TopicGetDto>> GetAllTopicsAsync()
    {
        return _mapper.Map<List<TopicGetDto>>(await _topicRepository.GetAllTopicsAsync());
    }

    public async Task<TopicGetDto> GetTopicByIdAsync(long id)
    {
        var topicByIdAsync = await _topicRepository.GetTopicByIdAsync(id);
        return _mapper.Map<TopicGetDto>(topicByIdAsync);
    }

    public async Task<TopicGetDto> UpdateTopicAsync(TopicUpdateDto topicUpdateDto)
    {
        var topic = await _topicRepository.GetTopicByIdAsync(topicUpdateDto.Id);
        if (topic is null)
        {
            return null;
        }
        
        var res = await _topicRepository.UpdateTopicAsync(topicUpdateDto);
        return _mapper.Map<TopicGetDto>(res);
    }

    public async Task<bool> DeleteTopicAsync(long id)
    {
        var topic = await _topicRepository.GetTopicByIdAsync(id);
        if (topic == null)
        {
            return false;
        }
        
        await _topicRepository.DeleteTopicAsync(id); 
        return true;
    }
}
