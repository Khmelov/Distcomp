using Riok.Mapperly.Abstractions;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;

namespace TaskREST.Mappers;

[Mapper]
public static partial class TweetMapper
{
    public static partial Tweet Map(UpdateTweetRequestTo updateTweetRequestTo);
    public static partial Tweet Map(CreateTweetRequestTo createTweetRequestTo);
    public static partial TweetResponseTo Map(Tweet tweet);
    public static partial IEnumerable<TweetResponseTo> Map(IEnumerable<Tweet> tweets);

    public static partial IEnumerable<Tweet> Map(
        IEnumerable<UpdateTweetRequestTo> tweetRequestTos);
}