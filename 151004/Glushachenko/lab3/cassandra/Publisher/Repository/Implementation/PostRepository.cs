using Publisher.Entity.Db;
using Publisher.Repository.Implementation.Common;
using Publisher.Repository.Interface;
using Publisher.Storage.Common;

namespace Publisher.Repository.Implementation
{
    public class PostRepository(DbStorage context) : AbstractCrudRepository<Post>(context), IPostRepository
    {
        private readonly DbStorage _context = context;

        public override Post Add(Post post)
        {
            var tweet = new Tweet { Id = post.TweetId };
            _context.Tweets.Attach(tweet);
            post.Tweet = tweet;

            return base.Add(post);
        }

        public override async Task<Post> AddAsync(Post post)
        {
            var tweet = new Tweet { Id = post.TweetId };
            _context.Tweets.Attach(tweet);
            post.Tweet = tweet;

            return await base.AddAsync(post);
        }
    }
}
