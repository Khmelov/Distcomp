using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Tweet.Queries;

public record class GetTweetByIdQuery(
    long id
) : IQuery<TweetProjection>;