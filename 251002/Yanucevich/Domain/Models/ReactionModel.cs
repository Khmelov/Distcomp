using System;

namespace Domain.Models;

public class ReactionModel
{
    public long Id { get; init; }
    public long TweetId { get; init; }
    public string Content { get; init; }
}
