namespace Domain.Projections;

public record class ReactionProjection
{
    public long Id { get; init; }
    public long TweetId { get; init; }
    public string Content { get; init; }
}
