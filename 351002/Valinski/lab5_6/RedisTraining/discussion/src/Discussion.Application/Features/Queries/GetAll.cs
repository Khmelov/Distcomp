using AutoMapper;
using Discussion.Application.Repositories;
using Discussion.Application.ViewModels;
using MediatR;
using Shared.Commons;

namespace Discussion.Application.Features.Queries;

public class GetAllQuery : IRequest<Result<List<ReactionResponseViewModel>>>
{
}

public class GetAllQueryHandler : IRequestHandler<GetAllQuery, Result<List<ReactionResponseViewModel>>>
{
    private readonly IMapper _mapper;
    private readonly IReactionRepository _repository;

    public GetAllQueryHandler(IMapper mapper, IReactionRepository repository)
    {
        _mapper = mapper;
        _repository = repository;
    }

    public async Task<Result<List<ReactionResponseViewModel>>> Handle(GetAllQuery request,
        CancellationToken cancellationToken)
    {
        var reactionsFromRepo = await _repository.GetAllAsync();
        var res = _mapper.Map<List<ReactionResponseViewModel>>(reactionsFromRepo);
        return Result<List<ReactionResponseViewModel>>.Success(res);
    }
}
