using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Tweet.Queries;

public record class GetTweetsQuery() : IQuery<IEnumerable<TweetProjection>>;