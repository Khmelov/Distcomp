using Publisher.Dtos; 
using System.Net.Http.Json;

namespace Publisher.Proxies
{
    public interface IReactionProxy
    {
        Task<ReactionResponseTo> GetByIdAsync(long topicId, long id);
        Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId);
        Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request);
        Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request);
        Task DeleteAsync(long topicId, long id);
        Task<ReactionResponseTo> GetByIdOnlyAsync(long id);
    }

    
}