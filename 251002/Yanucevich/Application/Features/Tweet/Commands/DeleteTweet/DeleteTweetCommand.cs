
using Application.Abstractions;

namespace Application.Features.Tweet.Commands;

public record class DeleteTweetCommand(
    long id
) : ICommand;