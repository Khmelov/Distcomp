using System.Reflection.Emit;
using Application.Dtos;
using Domain.Models;

namespace Application.Interfaces;

public interface IReactionRepository
{
    Task<List<Reaction>> GetAllReactionsAsync();
    Task<Reaction?> GetReactionByIdAsync(long id);
    Task<Reaction> CreateReactionAsync(ReactionCreateDto reactionCreateDto);
    Task DeleteReactionAsync (long id);
    Task<Reaction> UpdateReactionAsync(ReactionUpdateDto reactionUpdateDto);

}
