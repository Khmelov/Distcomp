using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetLabelById;

public class GetLabelByIdQueryHandler : IRequestHandler<GetLabelByIdQuery, Result<LabelResponseViewModel>>
{
    private readonly ILabelRepository _labelRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;
        
    public GetLabelByIdQueryHandler(ILabelRepository labelRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _labelRepository = labelRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }

    public async Task<Result<LabelResponseViewModel>> Handle(GetLabelByIdQuery request, CancellationToken cancellationToken)
    {
        var cachedLabel = await _redis.StringGetAsync($"labels:{request.Id}");
        if (cachedLabel.HasValue && !cachedLabel.IsNull)
        {
            var cachedResult = JsonSerializer.Deserialize<LabelResponseViewModel>(cachedLabel.ToString())!;
            return Result<LabelResponseViewModel>.Success(cachedResult);
        }
        
        var label = await _labelRepository.GetByIdAsync(request.Id);
        if (label == null)
        {
            return Result<LabelResponseViewModel>.Failure("Label not found", ErrorType.NotFound);
        }

        await _redis.StringSetAsync($"labels:{request.Id}", JsonSerializer.SerializeToUtf8Bytes(label));
        
        return Result<LabelResponseViewModel>.Success(_mapper.Map<LabelResponseViewModel>(label));
    }
}
