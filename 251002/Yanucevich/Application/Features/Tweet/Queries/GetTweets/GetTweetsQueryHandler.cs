using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Tweet.Queries;

public class GetTweetsQueryHandler : IQueryHandler<GetTweetsQuery, IEnumerable<TweetProjection>>
{
    private readonly ITweetRepository _tweetRepository;

    public GetTweetsQueryHandler(ITweetRepository tweetRepository)
    {
        _tweetRepository = tweetRepository;
    }

    public async Task<Result<IEnumerable<TweetProjection>>> Handle(GetTweetsQuery request, CancellationToken cancellationToken)
    {
        var resultTweets = await _tweetRepository.GetTweets();

        if(!resultTweets.IsSuccess){
            return Result.Failure<IEnumerable<TweetProjection>>(resultTweets.Error);
        }
        return Result.Success(resultTweets.Value.Select(t => t.ToTweetProjection()));
    }
}
