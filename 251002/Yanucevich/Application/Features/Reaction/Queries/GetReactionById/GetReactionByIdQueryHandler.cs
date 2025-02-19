using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Reaction.Queries;

public class GetReactionByIdQueryHandler : IQueryHandler<GetReactionByIdQuery, ReactionProjection>
{
    private readonly IReactionRepository _reactionRepository;

    public GetReactionByIdQueryHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<ReactionProjection>> Handle(GetReactionByIdQuery request, CancellationToken cancellationToken)
    {
        var resultReaction = await _reactionRepository.GetReactionById(request.id);

        if(!resultReaction.IsSuccess){
            return Result.Failure<ReactionProjection>(resultReaction.Error);
        }

        return resultReaction.Value.ToReactionProjection();
    }
}
