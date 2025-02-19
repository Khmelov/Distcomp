using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Reaction.Queries;

public record class GetReactionByIdQuery(
    long id
) : IQuery<ReactionProjection>;