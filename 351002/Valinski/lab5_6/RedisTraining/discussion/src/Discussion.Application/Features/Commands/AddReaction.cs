using AutoMapper;
using Discussion.Application.Repositories;
using Discussion.Application.ViewModels;
using Discussion.Domain.Models;
using MediatR;
using Shared.Commons;

namespace Discussion.Application.Features.Commands;

public class AddReactionCommand : IRequest<Result<ReactionResponseViewModel>>
{
    public required long Id { get; set; }
    public required long TopicId { get; set; }
    public required string Content { get; set; }
    public required string Country { get; set; }
}

public class AddReactionCommandHandler : IRequestHandler<AddReactionCommand, Result<ReactionResponseViewModel>>
{
    private readonly IMapper _mapper;
    private readonly IReactionRepository _reactionRepository;

    public AddReactionCommandHandler(IMapper mapper, IReactionRepository reactionRepository)
    {
        _mapper = mapper;
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(AddReactionCommand request,
        CancellationToken cancellationToken)
    {
        var reactionFromRepo = await _reactionRepository.GetByIdAsync(request.Id);
        if (reactionFromRepo != null)
        {
            return Result<ReactionResponseViewModel>.Failure("Reaction already exists", ErrorType.Conflict);
        }

        var reaction = _mapper.Map<Reaction>(request);
        await _reactionRepository.AddAsync(reaction);

        var res = _mapper.Map<ReactionResponseViewModel>(reaction);
        return Result<ReactionResponseViewModel>.Success(res);
    }
}
