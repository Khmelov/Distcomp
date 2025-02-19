using System.Windows.Input;
using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Tweet.Commands;

public record class UpdateTweetCommand(
    long id,
    long userId,
    string title,
    string content
) : ICommand<TweetProjection>;