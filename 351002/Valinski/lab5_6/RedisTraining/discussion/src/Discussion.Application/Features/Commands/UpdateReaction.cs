using AutoMapper;
using Discussion.Application.Repositories;
using Discussion.Application.ViewModels;
using MediatR;
using Shared.Commons;

namespace Discussion.Application.Features.Commands;

public class UpdateReactionCommand : IRequest<Result<ReactionResponseViewModel>>
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public string? Content { get; set; }
    public string? Country { get; set; }
}

public class UpdateReactionCommandHandler : IRequestHandler<UpdateReactionCommand, Result<ReactionResponseViewModel>>
{
    private readonly IReactionRepository _reactionRepository;
    private readonly IMapper _mapper;

    public UpdateReactionCommandHandler(IReactionRepository reactionRepository, IMapper mapper)
    {
        _reactionRepository = reactionRepository;
        _mapper = mapper;
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(UpdateReactionCommand request, CancellationToken cancellationToken)
    {
        var reaction = await _reactionRepository.GetByIdAsync(request.Id);
        if (reaction == null)
        {
            return Result<ReactionResponseViewModel>.Failure("Reaction was not found", ErrorType.NotFound);
        }

        reaction.Content = request.Content ?? reaction.Content;
        reaction.Country = request.Country ?? reaction.Country;
        reaction.TopicId = request.TopicId;
        var repoRes = await _reactionRepository.UpdateAsync(reaction);
        var res = _mapper.Map<ReactionResponseViewModel>(repoRes);
        
        return Result<ReactionResponseViewModel>.Success(res);
    }
}
