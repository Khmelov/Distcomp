using Publisher.Entity.Db;
using Publisher.Repository.Implementation.Common;
using Publisher.Repository.Interface;
using Publisher.Storage.Common;

namespace Publisher.Repository.Implementation
{
    public class TweetRepository(DbStorage context) : AbstractCrudRepository<Tweet>(context), ITweetRepository
    {
        private readonly DbStorage _context = context;

        public override Tweet Add(Tweet tweet)
        {
            var author = new Author { Id = tweet.AuthorId };
            _context.Authors.Attach(author);
            tweet.Author = author;

            return base.Add(tweet);
        }

        public override async Task<Tweet> AddAsync(Tweet tweet)
        {
            var author = new Author { Id = tweet.AuthorId };
            _context.Authors.Attach(author);
            tweet.Author = author;

            return await base.AddAsync(tweet);
        }
    }
}
