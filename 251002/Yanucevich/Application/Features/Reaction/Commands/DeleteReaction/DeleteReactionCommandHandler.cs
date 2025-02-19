using System;
using Application.Abstractions;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Reaction.Commands;

public class DeleteReactionCommandHandler : ICommandHandler<DeleteReactionCommand>
{
    private readonly IReactionRepository _reactionRepository;

    public DeleteReactionCommandHandler(IReactionRepository reactionRepository)
    {
        _reactionRepository = reactionRepository;
    }

    public async Task<Result> Handle(DeleteReactionCommand request, CancellationToken cancellationToken)
    {
        var result = await _reactionRepository.DeleteReaction(request.id);
    
        if(!result.IsSuccess){
            return Result.Failure(result.Error);
        }

        return result;
    }
}
