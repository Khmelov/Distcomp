using System;
using System.Net.Http.Headers;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Tweet.Commands;

public class CreateTweetCommandHandler : ICommandHandler<CreateTweetCommand, TweetProjection>
{
    private readonly ITweetRepository _tweetRepository;

    public CreateTweetCommandHandler(ITweetRepository tweetRepository)
    {
        _tweetRepository = tweetRepository;
    }

    public async Task<Result<TweetProjection>> Handle(CreateTweetCommand request, CancellationToken cancellationToken)
    {
        var resultTweet = await _tweetRepository.CreateTweet(new TweetModel{
            UserId = request.userId,
            Title = request.title, 
            Content = request.content
        });

        if (!resultTweet.IsSuccess){
            return Result.Failure<TweetProjection>(resultTweet.Error);
        }

        return resultTweet.Value.ToTweetProjection();
    }
}
