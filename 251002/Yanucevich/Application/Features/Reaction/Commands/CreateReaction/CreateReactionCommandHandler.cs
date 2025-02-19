using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Reaction.Commands;

public class CreateReactionCommandHandler : ICommandHandler<CreateReactionCommand, ReactionProjection>
{
    private readonly IReactionRepository _reactionRepository;

    public CreateReactionCommandHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<ReactionProjection>> Handle(CreateReactionCommand request, CancellationToken cancellationToken)
    {
        var resultReaction = await _reactionRepository.CreateReaction(
            new ReactionModel{
                TweetId = request.tweetId,
                Content = request.content
            });
        
        if(!resultReaction.IsSuccess){
            return Result.Failure<ReactionProjection>(resultReaction.Error);
        }

        return resultReaction.Value.ToReactionProjection();
    }
}
