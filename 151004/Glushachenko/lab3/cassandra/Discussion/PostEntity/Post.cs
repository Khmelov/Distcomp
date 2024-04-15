using Discussion.Common;

namespace Discussion.PostEntity
{
    public class Post(string content, string country) : AbstractEntity
    {
        public string Country { get; set; } = country;
        public int TweetId { get; set; }
        public string Content { get; set; } = content;

        public Post() : this(string.Empty, string.Empty) { }

        public Post(int id, int tweetId, string content) : this(content, string.Empty)
        {
            Id = id;
            TweetId = tweetId;
        }

        public Post(int id, int tweetId, string content, string country) : this(content, country)
        {
            Id = id;
            TweetId = tweetId;
        }
    }
}
