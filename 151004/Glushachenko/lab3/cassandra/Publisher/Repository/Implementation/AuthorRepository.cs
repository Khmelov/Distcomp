using Publisher.Entity.Db;
using Publisher.Repository.Implementation.Common;
using Publisher.Repository.Interface;
using Publisher.Storage.Common;

namespace Publisher.Repository.Implementation
{
    public class AuthorRepository(DbStorage context) : AbstractCrudRepository<Author>(context), IAuthorRepository
    {
        public Tweet GetByTweetId(int tweetId)
        {
            throw new NotImplementedException();
        }

        public Task<Tweet> GetByTweetIdAsync(int tweetId)
        {
            throw new NotImplementedException();
        }
    }
}
