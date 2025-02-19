using System;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;

namespace Infrastructure.Repositories.InMemory;

public class TweetRepositoryInMemory : ITweetRepository
{
    private Dictionary<long, TweetModel> _tweets = new();
    private long _id = 0;

    public Task<Result<TweetModel>> CreateTweet(TweetModel tweet)
    {
        if(_tweets.Values.Where(t => t.Title == tweet.Title).Any()){
            return Task.FromResult(Result.Failure<TweetModel>(TweetErrors.TweetNotUniqueError));
        }

        var time = DateTime.Now;
        _tweets.Add(_id, new TweetModel{
            Id = _id,
            UserId = tweet.UserId,
            Title = tweet.Title, 
            Content = tweet.Content, 
            Created = time,
            Modified = time,
        });

        return Task.FromResult(Result.Success(_tweets[_id++]));
    }

    public Task<Result> DeleteTweet(long id)
    {
        return Task.FromResult(
            _tweets.Remove(id)
                ? Result.Success()
                : Result.Failure(TweetErrors.TweetNotFoundError)
        );
    }

    public Task<Result<TweetModel>> GetTweetById(long id)
    {
        return Task.FromResult(
            _tweets.TryGetValue(id, out var result)
                ? Result.Success(result)
                : Result.Failure<TweetModel>(TweetErrors.TweetNotFoundError)
        );
    }

    public Task<Result<IEnumerable<TweetModel>>> GetTweets()
    {
        return Task.FromResult(Result.Success<IEnumerable<TweetModel>>(_tweets.Values));
    }

    public Task<Result<TweetModel>> UpdateTweet(TweetModel tweet)
    {
        if(!_tweets.ContainsKey(tweet.Id)){
            return Task.FromResult(Result.Failure<TweetModel>(
                TweetErrors.TweetNotFoundError
            ));
        }

        if(_tweets.Values.Where(t => t.Title == tweet.Title && t.Id != tweet.Id).Any()){
            return Task.FromResult(Result.Failure<TweetModel>(
                TweetErrors.TweetNotUniqueError
            ));
        }

        TweetModel newModel = new TweetModel{
            Id = tweet.Id,
            UserId = tweet.UserId,
            Title = tweet.Title,
            Content = tweet.Content,
            Created = _tweets[tweet.Id].Created,
            Modified = DateTime.UtcNow
        };
        _tweets[tweet.Id] = newModel;

        return Task.FromResult(Result.Success(_tweets[tweet.Id]));
    }
}
