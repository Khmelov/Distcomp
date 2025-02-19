using System;
using Domain.Models;
using Domain.Shared;

namespace Domain.Repositories;

public interface ITweetRepository
{
    Task<Result<TweetModel>> CreateTweet(TweetModel tweet);
    Task<Result<IEnumerable<TweetModel>>> GetTweets();
    Task<Result<TweetModel>> GetTweetById(long id);
    Task<Result<TweetModel>> UpdateTweet(TweetModel tweet);
    Task<Result> DeleteTweet(long id); 
}
