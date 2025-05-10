using WebApplication1.DTO;

namespace WebApplication1.Service
{
    public interface IRemoteReactionService
    {
        Task<ReactionResponseTo> CreateReactionAsync(ReactionRequestTo dto);
        Task<ReactionResponseTo> GetReactionByIdAsync(long id);
        Task<List<ReactionResponseTo>> GetAllReactionsAsync();
        Task<ReactionResponseTo> UpdateReactionAsync(ReactionRequestTo dto);
        Task DeleteReactionAsync(string id);
    }
}
