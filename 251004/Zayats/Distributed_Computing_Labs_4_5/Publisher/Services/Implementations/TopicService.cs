using AutoMapper;
using FluentValidation;
using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;
using Publisher.Exceptions;
using Publisher.Infrastructure.Validators;
using Publisher.Models;
using Publisher.Repositories.Interfaces;
using Publisher.Services.Interfaces;

namespace Publisher.Services.Implementations;

public class TopicService : ITopicService
{
    private readonly ITopicRepository _iTopicRepository;
    private readonly IAuthorRepository _iAuthorRepository;
    private readonly IMapper _mapper;
    private readonly TopicRequestDTOValidator _validator;

    public TopicService(ITopicRepository iTopicRepository, IAuthorRepository iAuthorRepository,
        IMapper mapper, TopicRequestDTOValidator validator)
    {
        _iTopicRepository = iTopicRepository;
        _iAuthorRepository = iAuthorRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<TopicResponseDTO>> GetStoriesAsync()
    {
        var stories = await _iTopicRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<TopicResponseDTO>>(stories);
    }

    public async Task<TopicResponseDTO> GetStoryByIdAsync(long id)
    {
        var story = await _iTopicRepository.GetByIdAsync(id)
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
        
        var createdStory = await _iTopicRepository.CreateAsync(storyToCreate);
        return _mapper.Map<TopicResponseDTO>(createdStory);
    }

    public async Task<TopicResponseDTO> UpdateStoryAsync(TopicRequestDTO topic)
    {
        await _validator.ValidateAndThrowAsync(topic);
        var storyToUpdate = _mapper.Map<Topic>(topic);
        
        storyToUpdate.Modified = DateTime.UtcNow;
        storyToUpdate.Created = DateTime.UtcNow;
        
        var updatedStory = await _iTopicRepository.UpdateAsync(storyToUpdate)
                           ?? throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(topic.Id));
        return _mapper.Map<TopicResponseDTO>(updatedStory);
    }

    public async Task DeleteStoryAsync(long id)
    {
        if (!await _iTopicRepository.DeleteAsync(id))
        {
            throw new NotFoundException(ErrorCodes.StoryNotFound, ErrorMessages.StoryNotFoundMessage(id));
        }
    }
}