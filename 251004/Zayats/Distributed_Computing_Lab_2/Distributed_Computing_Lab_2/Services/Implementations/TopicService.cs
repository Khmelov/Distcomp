using AutoMapper;
using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.DTO.ResponseDTO;
using Distributed_Computing_Lab_2.Exceptions;
using Distributed_Computing_Lab_2.Infrastructure.Validators;
using Distributed_Computing_Lab_2.Models;
using Distributed_Computing_Lab_2.Repositories.Interfaces;
using Distributed_Computing_Lab_2.Services.Interfaces;
using FluentValidation;

namespace Distributed_Computing_Lab_2.Services.Implementations;

public class TopicService : ITopicService
{
    private readonly ITopicRepository _topicRepository;
    private readonly IAuthorRepository _authorRepository;
    private readonly IMapper _mapper;
    private readonly TopicRequestDTOValidator _validator;

    public TopicService(ITopicRepository topicRepository, IAuthorRepository authorRepository,
        IMapper mapper, TopicRequestDTOValidator validator)
    {
        _topicRepository = topicRepository;
        _authorRepository = authorRepository;
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

        storyToCreate.AuthorId = topic.AuthorId;
        storyToCreate.Created = DateTime.UtcNow;
        storyToCreate.Modified = DateTime.UtcNow;
        
        var createdStory = await _topicRepository.CreateAsync(storyToCreate);
        return _mapper.Map<TopicResponseDTO>(createdStory);
    }

    public async Task<TopicResponseDTO> UpdateStoryAsync(TopicRequestDTO topic)
    {
        await _validator.ValidateAndThrowAsync(topic);
        var storyToUpdate = _mapper.Map<Topic>(topic);
        
        storyToUpdate.Modified = DateTime.UtcNow;
        
        var updatedStory = await _topicRepository.UpdateAsync(storyToUpdate)
                           ?? throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(topic.Id));
        return _mapper.Map<TopicResponseDTO>(updatedStory);
    }

    public async Task DeleteStoryAsync(long id)
    {
        if (!await _topicRepository.DeleteAsync(id))
        {
            throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(id));
        }
    }
}