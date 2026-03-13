using Application.Abstractions;
using Application.Dtos;
using Application.Interfaces;
using AutoMapper;

namespace Application.Services;

public class ReactionService : IReactionService
{
    private readonly IReactionRepository _reactionRepository;
    private readonly ITopicRepository _topicRepository;
    private readonly IMapper _mapper;

    public ReactionService(IReactionRepository reactionRepository, IMapper mapper, ITopicRepository topicRepository)
    {
        _reactionRepository = reactionRepository;
        _mapper = mapper;
        _topicRepository = topicRepository;
    }

    public async Task<ReactionGetDto?> CreateReactionAsync(ReactionCreateDto reactionCreateDto)
    {
        var topic = await _topicRepository.GetTopicByIdAsync(reactionCreateDto.TopicId);
        if (topic == null)
            return null;
            
        
        var reaction = await _reactionRepository.CreateReactionAsync(reactionCreateDto);
        return _mapper.Map<ReactionGetDto>(reaction);
    }

    public async Task<List<ReactionGetDto>> GetAllReactionsAsync()
    {
        return _mapper.Map<List<ReactionGetDto>>(await _reactionRepository.GetAllReactionsAsync());
    }

    public async Task<ReactionGetDto> GetReactionByIdAsync(long id)
    {
        var reaction = await _reactionRepository.GetReactionByIdAsync(id);
        return _mapper.Map<ReactionGetDto>(reaction);
    }

    public async Task<ReactionGetDto> UpdateReactionAsync(ReactionUpdateDto reactionUpdateDto)
    {
        if (await _reactionRepository.GetReactionByIdAsync(reactionUpdateDto.Id) == null)
        {
            return null;
        }

        var updated = await _reactionRepository.UpdateReactionAsync(reactionUpdateDto);
        return _mapper.Map<ReactionGetDto>(updated);
    }

    public async Task<bool> DeleteReactionAsync(long id)
    {
        if (await _reactionRepository.GetReactionByIdAsync(id) == null)
        {
            return false;
        }

        await _reactionRepository.DeleteReactionAsync(id);
        return true;
    }
}
