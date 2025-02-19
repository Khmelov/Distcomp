namespace Domain.Projections;

public record class TweetProjection
{
    public long Id { get; init; }
    public long UserId { get; init;}
    public string Title { get; init; }
    public string Content { get; init; }
    public DateTime Created { get; init; }
    public DateTime Modified { get; init; }
}
