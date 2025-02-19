using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Reaction.Queries;

public record class GetReactionsQuery : IQuery<IEnumerable<ReactionProjection>>;