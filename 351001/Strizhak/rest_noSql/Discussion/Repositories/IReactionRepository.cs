using Discussion.Entities;

namespace Discussion.Repositories
{
    public interface IReactionRepository
    {
        Task<Reaction?> GetByIdAsync(long topicId, long id);
        Task<IEnumerable<Reaction>> GetByTopicIdAsync(long topicId);
        Task AddAsync(Reaction entity);
        Task UpdateAsync(Reaction entity);
        Task DeleteAsync(long topicId, long id);
        Task<Reaction?> GetByIdOnlyAsync(long id);
    }
}
