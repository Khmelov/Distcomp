using Publisher.Entity.Common;
using System.ComponentModel.DataAnnotations;

namespace Publisher.Entity.Db
{
    public class Post(string content) : AbstractEntity
    {
        public int TweetId { get; set; }
        [MinLength(2)]
        public string Content { get; set; } = content;
        public Tweet Tweet { get; set; } = null!;

        public Post() : this(string.Empty) { }
        public Post(int id, int tweetId, string content, Tweet tweet) : this(content)
        {
            Id = id;
            TweetId = tweetId;
            Tweet = tweet;
        }
    }
}
