using AutoMapper;
using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;
using Distributed_Computing_Lab_1.Exceptions;
using Distributed_Computing_Lab_1.Infrastructure.Validators;
using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;
using Distributed_Computing_Lab_1.Services.Interfaces;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Services.Implementations;

public class TopicService : ITopicService
{
    private readonly ITopicRepository _topicRepository;
    private readonly IMapper _mapper;
    private readonly TopicRequestDTOValidator _validator;

    public TopicService(ITopicRepository topicRepository,
        IMapper mapper, TopicRequestDTOValidator validator)
    {
        _topicRepository = topicRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<TopicResponseDTO>> GetStoriesAsync()
    {
        var stories = await _topicRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<TopicResponseDTO>>(stories);
    }

    public async Task<TopicResponseDTO> GetStoryByIdAsync(long id)
    {
        var story = await _topicRepository.GetByIdAsync(id)
                    ?? throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(id));
        return _mapper.Map<TopicResponseDTO>(story);
    }

    public async Task<TopicResponseDTO> CreateStoryAsync(TopicRequestDTO topic)
    {
        await _validator.ValidateAndThrowAsync(topic);
        var storyToCreate = _mapper.Map<Topic>(topic);
        
        storyToCreate.Created = DateTime.Now;
        storyToCreate.Modified = DateTime.Now;
        
        var createdStory = await _topicRepository.CreateAsync(storyToCreate);
        return _mapper.Map<TopicResponseDTO>(createdStory);
    }

    public async Task<TopicResponseDTO> UpdateStoryAsync(TopicRequestDTO topic)
    {
        await _validator.ValidateAndThrowAsync(topic);
        var storyToUpdate = _mapper.Map<Topic>(topic);
        
        storyToUpdate.Modified = DateTime.Now;
        
        var updatedStory = await _topicRepository.UpdateAsync(storyToUpdate)
                           ?? throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(topic.Id));
        return _mapper.Map<TopicResponseDTO>(updatedStory);
    }

    public async Task DeleteStoryAsync(long id)
    {
        if (await _topicRepository.DeleteAsync(id) is null)
        {
            throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(id));
        }
    }
}