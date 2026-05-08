using Domain.Models;

namespace Domain.Abstractions;

public interface IReactionRepository
{
    Task<List<Reaction>> GetAllReactions();
    Task<Reaction> CreateReaction(Reaction reaction);
    Task<Reaction?> GetById(long id);
    Task<Reaction> UpdateReaction(Reaction reaction);
    Task DeleteReaction(long id, long topicid, string country);
}
