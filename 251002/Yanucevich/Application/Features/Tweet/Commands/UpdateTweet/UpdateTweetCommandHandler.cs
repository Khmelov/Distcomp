using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Tweet.Commands;

public class UpdateTweetCommandHandler : ICommandHandler<UpdateTweetCommand, TweetProjection>
{
    private readonly ITweetRepository _tweetRepository;

    public UpdateTweetCommandHandler(ITweetRepository tweetRepository)
    {
        _tweetRepository = tweetRepository;
    }

    public async Task<Result<TweetProjection>> Handle(UpdateTweetCommand request, CancellationToken cancellationToken)
    {
        var resultTweet = await _tweetRepository.UpdateTweet(new TweetModel{
            Id = request.id,
            UserId = request.userId,
            Title = request.title,
            Content = request.content
        });

        if(!resultTweet.IsSuccess){
            return Result.Failure<TweetProjection>(resultTweet.Error);
        }

        return resultTweet.Value.ToTweetProjection();
    }
}
