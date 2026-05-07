using Application.Dtos;

namespace Application.Abstractions;

public interface IReactionService
{
    Task<ReactionGetDto> CreateReactionAsync(ReactionCreateDto reactionCreateDto);
    Task<List<ReactionGetDto>> GetAllReactionsAsync();
    Task<ReactionGetDto> GetReactionByIdAsync(long id);
    Task<ReactionGetDto> UpdateReactionAsync(ReactionUpdateDto reactionUpdateDto);
    Task<bool> DeleteReactionAsync(long id);
}
