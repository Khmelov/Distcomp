using Discussion.Common.Interface;

namespace Discussion.PostEntity.Interface
{
    public interface IPostService : ICrudService<Post, PostRequestTO, PostResponseTO>
    {
        Task<IList<Post>> GetByTweetID(int tweetId);
    }
}
