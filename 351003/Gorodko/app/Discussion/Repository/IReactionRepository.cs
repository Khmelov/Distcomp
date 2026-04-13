using Discussion.Model;

namespace Discussion.Repository {
    public interface IReactionRepository {
        Task<Reaction?> GetAsync(string country, long tweetId, long id);
        Task<IEnumerable<Reaction>> GetByTweetIdAsync(string country, long tweetId);
        Task<IEnumerable<Reaction>> GetByCountryAsync(string country);
        Task<Reaction> AddAsync(Reaction reaction);
        Task<Reaction> UpdateAsync(Reaction reaction);
        Task<bool> DeleteAsync(string country, long tweetId, long id);
        Task<bool> ExistsAsync(string country, long tweetId, long id);
        Task<IEnumerable<Reaction>> FindByIdAsync(long id);
    }
}