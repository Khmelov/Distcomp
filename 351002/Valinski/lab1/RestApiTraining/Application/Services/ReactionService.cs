using Application.Abstractions;
using Application.Dtos;

namespace Application.Services;

public class ReactionService : IReactionService
{
    public async Task<ReactionGetDto> CreateReactionAsync(ReactionCreateDto reactionCreateDto)
    {
        throw new NotImplementedException();
    }

    public async Task<List<ReactionGetDto>> GetAllReactionsAsync()
    {
        throw new NotImplementedException();
    }

    public async Task<ReactionGetDto> GetReactionByIdAsync(long id)
    {
        throw new NotImplementedException();
    }

    public async Task<ReactionGetDto> UpdateReactionAsync(ReactionUpdateDto reactionUpdateDto)
    {
        throw new NotImplementedException();
    }

    public async Task<bool> DeleteReactionAsync(long id)
    {
        throw new NotImplementedException();
    }
}
