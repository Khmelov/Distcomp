using Shared.Dtos;

namespace Discussion.Services
{
    public interface IReactionService
    {
        Task<ReactionResponseTo?> GetByIdAsync(long topicId, long id);
        Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId);
        Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request);
        Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request);
        Task DeleteAsync(long topicId, long id);
        Task<ReactionResponseTo?> GetByIdOnlyAsync(long id);
    }
}
