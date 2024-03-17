using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;

namespace TaskREST.Services.Interfaces;

public interface ITweetService
{
    Task<TweetResponseTo> GetTweetById(long id);
    Task<IEnumerable<TweetResponseTo>> GetTweets();
    Task<TweetResponseTo> CreateTweet(CreateTweetRequestTo createTweetRequestTo);
    Task DeleteTweet(long id);
    Task<TweetResponseTo> UpdateTweet(UpdateTweetRequestTo modifiedTweet);
}