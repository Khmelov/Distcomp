using AutoMapper;
using Discussion.Application.Repositories;
using Discussion.Application.ViewModels;
using MediatR;
using Shared.Commons;

namespace Discussion.Application.Features.Queries;

public class GetByIdQuery : IRequest<Result<ReactionResponseViewModel>>
{
    public required long Id;
}

public class GetByIdQueryHandler : IRequestHandler<GetByIdQuery, Result<ReactionResponseViewModel>>
{
    private readonly IMapper _mapper;
    private readonly IReactionRepository _reactionRepository;

    public GetByIdQueryHandler(IMapper mapper, IReactionRepository reactionRepository)
    {
        _mapper = mapper;
        _reactionRepository = reactionRepository;
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(GetByIdQuery request,
        CancellationToken cancellationToken)
    {
        var reaction = await _reactionRepository.GetByIdAsync(request.Id);
        if (reaction == null)
        {
            return Result<ReactionResponseViewModel>.Failure("Reaction was not found", ErrorType.NotFound);
        }

        var res = _mapper.Map<ReactionResponseViewModel>(reaction);
        return Result<ReactionResponseViewModel>.Success(res);
    }
}
