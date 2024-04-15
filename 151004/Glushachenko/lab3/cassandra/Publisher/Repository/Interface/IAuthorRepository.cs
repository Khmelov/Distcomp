using Publisher.Entity.Db;
using Publisher.Repository.Interface.Common;

namespace Publisher.Repository.Interface
{
    public interface IAuthorRepository : ICrudRepository<Author>
    {
        Tweet GetByTweetId(int tweetId);
        Task<Tweet> GetByTweetIdAsync(int tweetId);
    }
}
