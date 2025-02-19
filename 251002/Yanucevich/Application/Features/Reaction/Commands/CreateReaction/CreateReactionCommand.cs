using System.Windows.Input;
using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Reaction.Commands;

public record class CreateReactionCommand(
    long tweetId,
    string content
) : ICommand<ReactionProjection>;