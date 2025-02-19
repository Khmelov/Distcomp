using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Reaction.Commands;

public class UpdateReactionCommandHandler : ICommandHandler<UpdateReactionCommand, ReactionProjection>
{
    private readonly IReactionRepository _reactionRepository;

    public UpdateReactionCommandHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<ReactionProjection>> Handle(UpdateReactionCommand request, CancellationToken cancellationToken)
    {
        var resultReaction = await _reactionRepository.UpdateReaction(
            new ReactionModel{
                Id = request.id,
                TweetId = request.tweetId,
                Content = request.content
            }
        );
    
        if(!resultReaction.IsSuccess){
            return Result.Failure<ReactionProjection>(resultReaction.Error);
        }

        return resultReaction.Value.ToReactionProjection();
    }
}
