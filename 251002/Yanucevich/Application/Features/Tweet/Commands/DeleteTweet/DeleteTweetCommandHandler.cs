using System;
using Application.Abstractions;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Tweet.Commands;

public class DeleteTweetCommandHandler : ICommandHandler<DeleteTweetCommand>
{
    private readonly ITweetRepository _tweetRepository;

    public DeleteTweetCommandHandler(ITweetRepository tweetRepository)
    {
        _tweetRepository = tweetRepository;
    }

    public async Task<Result> Handle(DeleteTweetCommand request, CancellationToken cancellationToken)
    {
        var result = await _tweetRepository.DeleteTweet(request.id);

        if(!result.IsSuccess){
            return Result.Failure(result.Error);
        }

        return result;
    }
}
