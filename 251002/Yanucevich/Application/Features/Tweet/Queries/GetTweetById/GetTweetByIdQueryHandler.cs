using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Tweet.Queries;

public class GetTweetByIdQueryHandler : IQueryHandler<GetTweetByIdQuery, TweetProjection>
{
    private readonly ITweetRepository _tweetRepository;

    public GetTweetByIdQueryHandler(ITweetRepository tweetRepository)
    {
        _tweetRepository = tweetRepository;
    }

    public async Task<Result<TweetProjection>> Handle(GetTweetByIdQuery request, CancellationToken cancellationToken)
    {
        var resultTweet = await _tweetRepository.GetTweetById(request.id);

        if(!resultTweet.IsSuccess){
            return Result.Failure<TweetProjection>(resultTweet.Error);
        }

        return resultTweet.Value.ToTweetProjection();
    }

}
