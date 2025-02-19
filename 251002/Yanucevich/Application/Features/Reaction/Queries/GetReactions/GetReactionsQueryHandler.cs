using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Reaction.Queries;

public class GetReactionsQueryHandler : IQueryHandler<GetReactionsQuery, IEnumerable<ReactionProjection>>
{
    private readonly IReactionRepository _reactionRepository;

    public GetReactionsQueryHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<IEnumerable<ReactionProjection>>> Handle(GetReactionsQuery request, CancellationToken cancellationToken)
    {
        var resultReactions = await _reactionRepository.GetReactions();

        if(!resultReactions.IsSuccess){
            return Result.Failure<IEnumerable<ReactionProjection>>(resultReactions.Error);
        }

        return Result.Success(resultReactions.Value.Select(r => r.ToReactionProjection()));
    }
}
