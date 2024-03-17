using Microsoft.EntityFrameworkCore;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Exceptions;
using TaskREST.Services.Interfaces;
using TaskREST.Storage;
using TweetMapper = TaskREST.Mappers.TweetMapper;

namespace TaskREST.Services.Implementations;

public sealed class TweetService(AppDbContext context) : ITweetService
{
    public async Task<TweetResponseTo> GetTweetById(long id)
    {
        var tweet = await context.Tweets.FindAsync(id);
        if (tweet == null) throw new EntityNotFoundException($"Tweet with id = {id} doesn't exist.");

        return TweetMapper.Map(tweet);
    }

    public async Task<IEnumerable<TweetResponseTo>> GetTweets()
    {
        return TweetMapper.Map(await context.Tweets.ToListAsync());
    }

    public async Task<TweetResponseTo> CreateTweet(CreateTweetRequestTo createTweetRequestTo)
    {
        var tweet = TweetMapper.Map(createTweetRequestTo);
        await context.Tweets.AddAsync(tweet);
        await context.SaveChangesAsync();
        return TweetMapper.Map(tweet);
    }

    public async Task DeleteTweet(long id)
    {
        var tweet = await context.Tweets.FindAsync(id);
        if (tweet == null) throw new EntityNotFoundException($"Tweet with id = {id} doesn't exist.");

        context.Tweets.Remove(tweet);
        await context.SaveChangesAsync();
    }

    public async Task<TweetResponseTo> UpdateTweet(UpdateTweetRequestTo modifiedTweet)
    {
        var tweet = await context.Tweets.FindAsync(modifiedTweet.Id);
        if (tweet == null) throw new EntityNotFoundException($"Tweet with id = {modifiedTweet.Id} doesn't exist.");

        context.Entry(tweet).State = EntityState.Modified;

        tweet.Id = modifiedTweet.Id;
        tweet.Content = modifiedTweet.Content;
        tweet.CreatorId = modifiedTweet.CreatorId;
        tweet.Title = modifiedTweet.Title;


        await context.SaveChangesAsync();
        return TweetMapper.Map(tweet);
    }
}