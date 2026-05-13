using Discussion.Application.Repositories;
using MediatR;
using Shared.Commons;

namespace Discussion.Application.Features.Commands;

public class DeleteReactionCommand : IRequest<Result>
{
    public required long Id;
}

public class DeleteReactionCommandHandler : IRequestHandler<DeleteReactionCommand, Result>
{
    private readonly IReactionRepository _reactionRepository;

    public DeleteReactionCommandHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result> Handle(DeleteReactionCommand request, CancellationToken cancellationToken)
    {
        var reaction = await _reactionRepository.GetByIdAsync(request.Id);
        if (reaction == null)
        {
            return Result.Failure("Reaction was not found", ErrorType.NotFound);
        }

        await _reactionRepository.DeleteAsync(reaction);
        return Result.Success();
    }
}
