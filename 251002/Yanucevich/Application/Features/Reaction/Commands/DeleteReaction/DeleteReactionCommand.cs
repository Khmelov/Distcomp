using Application.Abstractions;

namespace Application.Features.Reaction.Commands;

public record class DeleteReactionCommand(
    long id
) : ICommand;