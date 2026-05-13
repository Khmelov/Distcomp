using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetAllLabels;

public class GetAllLabelsQueryHandler : IRequestHandler<GetAllLabelsQuery, Result<List<LabelResponseViewModel>>>
{
    private readonly IMapper _mapper;
    private readonly ILabelRepository _labelRepository;
    private readonly IDatabase _database;

    public GetAllLabelsQueryHandler(IConnectionMultiplexer conn, ILabelRepository labelRepository, IMapper mapper)
    {
        _labelRepository = labelRepository;
        _mapper = mapper;
        _database = conn.GetDatabase();
    }

    public async Task<Result<List<LabelResponseViewModel>>> Handle(GetAllLabelsQuery request, CancellationToken cancellationToken)
    {
        // todo: make data retrieving from cache

        var tempResult = await _labelRepository.GetAllAsync();
        
        return Result<List<LabelResponseViewModel>>.Success(_mapper.Map<List<LabelResponseViewModel>>(tempResult));
    }
}
